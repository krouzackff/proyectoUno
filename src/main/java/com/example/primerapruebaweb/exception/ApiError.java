package com.example.primerapruebaweb.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        this.timestamp = LocalDateTime.now();
    }

    public abstract static class ApiSubError {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiValidationError extends ApiSubError {
        private String object;
        private String field;
        private Object rejectedValue;
        private String message;

        public ApiValidationError(String object, String field, Object rejectedValue, String message) {
            this.object = object;
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }
}