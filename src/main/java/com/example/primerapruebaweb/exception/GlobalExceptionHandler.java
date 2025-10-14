package com.example.primerapruebaweb.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Manejo de excepciones genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Error no manejado: ", ex);

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor",
                ex
        );

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Manejo de entidades no encontradas
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entidad no encontrada: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                ex
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // Manejo de violación de restricciones de base de datos (unique, foreign key, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Violación de integridad de datos: ", ex);

        String message = "Error de integridad de datos";

        // Detectar tipo específico de violación
        if (ex.getMessage().contains("unique constraint") || ex.getMessage().contains("Duplicate entry")) {
            message = "Ya existe un registro con los mismos datos";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "No se puede eliminar/actualizar debido a referencias existentes";
        }

        ApiError apiError = new ApiError(HttpStatus.CONFLICT, message, ex);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                ex
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // Manejo de validación de constraints JPA
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Violación de constraint: {}", ex.getMessage());

        List<ApiError.ApiValidationError> validationErrors = ex.getConstraintViolations().stream()
                .map(violation -> new ApiError.ApiValidationError(
                        violation.getRootBeanClass().getSimpleName(),
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Error de validación");
        apiError.setSubErrors(validationErrors.stream()
                .map(error -> (ApiError.ApiSubError) error)
                .collect(Collectors.toList()));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones personalizadas para negocio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex) {
        log.warn("Excepción de negocio: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ex
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Sobrescribir manejo de validación de argumentos (@Valid)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("Argumento de método no válido: {}", ex.getMessage());

        List<ApiError.ApiValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldErrorToValidationError)
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Error de validación en los datos de entrada");
        apiError.setSubErrors(validationErrors.stream()
                .map(error -> (ApiError.ApiSubError) error)
                .collect(Collectors.toList()));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Sobrescribir manejo de mensaje HTTP no legible
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("Mensaje HTTP no legible: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Solicitud JSON mal formada",
                ex
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    private ApiError.ApiValidationError mapFieldErrorToValidationError(FieldError fieldError) {
        return new ApiError.ApiValidationError(
                fieldError.getObjectName(),
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
        );
    }
}