package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.PassengerDTO;
import com.example.primerapruebaweb.entity.Passenger;
import com.example.primerapruebaweb.exception.BusinessException;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.PassengerRepository;
import com.example.primerapruebaweb.services.mapper.PassengerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional
    public PassengerDTO.PassengerResponse create(PassengerDTO.PassengerRequest request) {
        try {
            log.info("Creando nuevo pasajero: {}", request.fullname());

            // Validaciones de negocio
            validatePassengerBusinessRules(request);

            Passenger passenger = passengerMapper.toEntity(request);
            Passenger passengerSaved = passengerRepository.save(passenger);

            log.info("Pasajero creado exitosamente con ID: {}", passengerSaved.getId());
            return passengerMapper.toResponse(passengerSaved);

        } catch (BusinessException ex) {
            log.warn("Error de negocio al crear pasajero: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al crear pasajero", ex);
            throw new BusinessException("Error al crear el pasajero", ex);
        }
    }

    @Override
    @Transactional
    public PassengerDTO.PassengerResponse update(Long id, PassengerDTO.PassengerUpdateRequest request) {
        try {
            log.info("Actualizando pasajero con ID: {}", id);

            Passenger passenger = passengerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pasajero", id));

            // Validaciones de negocio para actualización
            validatePassengerUpdateBusinessRules(passenger, request);

            passengerMapper.updateEntity(passenger, request);
            Passenger updatedPassenger = passengerRepository.save(passenger);

            log.info("Pasajero actualizado exitosamente: {}", updatedPassenger.getId());
            return passengerMapper.toResponse(updatedPassenger);

        } catch (ResourceNotFoundException ex) {
            log.warn("Pasajero no encontrado para actualizar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al actualizar pasajero: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al actualizar pasajero ID: {}", id, ex);
            throw new BusinessException("Error al actualizar el pasajero", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerDTO.PassengerResponse findById(Long id) {
        try {
            log.debug("Buscando pasajero con ID: {}", id);

            return passengerRepository.findById(id)
                    .map(passenger -> {
                        log.debug("Pasajero encontrado: {}", passenger.getFullName());
                        return passengerMapper.toResponse(passenger);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Pasajero", id));

        } catch (ResourceNotFoundException ex) {
            log.warn("Pasajero no encontrado: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al buscar pasajero ID: {}", id, ex);
            throw new BusinessException("Error al buscar el pasajero", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerDTO.PassengerResponse> findAll() {
        try {
            log.debug("Obteniendo todos los pasajeros");
            List<PassengerDTO.PassengerResponse> passengers = passengerRepository.findAll()
                    .stream()
                    .map(passengerMapper::toResponse)
                    .toList();

            log.debug("Se encontraron {} pasajeros", passengers.size());
            return passengers;

        } catch (Exception ex) {
            log.error("Error inesperado al obtener todos los pasajeros", ex);
            throw new BusinessException("Error al obtener los pasajeros", ex);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            log.info("Eliminando pasajero con ID: {}", id);

            Passenger passenger = passengerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pasajero", id));

            // Validaciones de negocio antes de eliminar
            if (hasActiveBookings(passenger)) {
                throw new BusinessException("No se puede eliminar el pasajero porque tiene reservas activas");
            }

            passengerRepository.delete(passenger);
            log.info("Pasajero eliminado exitosamente: {}", id);

        } catch (ResourceNotFoundException ex) {
            log.warn("Pasajero no encontrado para eliminar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al eliminar pasajero: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al eliminar pasajero ID: {}", id, ex);
            throw new BusinessException("Error al eliminar el pasajero", ex);
        }
    }

    // Métodos auxiliares para validaciones de negocio
    private void validatePassengerBusinessRules(PassengerDTO.PassengerRequest request) {
        // Validar campos obligatorios
        if (request.fullname() == null || request.fullname().trim().isEmpty()) {
            throw new BusinessException("El nombre completo del pasajero es obligatorio");
        }

        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new BusinessException("El email del pasajero es obligatorio");
        }

        // Validar formato de email
        if (!isValidEmail(request.email())) {
            throw new BusinessException("El formato del email no es válido");
        }

        // Validar que no exista un pasajero con el mismo email
        if (passengerRepository.existsByEmail((request.email()))) {
            throw new BusinessException("Ya existe un pasajero con el email: " + request.email());
        }

        // Validar perfil si está presente
        if (request.profile() != null) {
            validatePassengerProfile(request.profile());
        }
    }

    private void validatePassengerUpdateBusinessRules(Passenger passenger, PassengerDTO.PassengerUpdateRequest request) {
        // Validar campos si se proporcionan
        if (request.fullname() != null && request.fullname().trim().isEmpty()) {
            throw new BusinessException("El nombre completo no puede estar vacío");
        }

        // Validar email si se proporciona y es diferente al actual
        if (request.email() != null && !request.email().equals(passenger.getEmail())) {
            if (!isValidEmail(request.email())) {
                throw new BusinessException("El formato del email no es válido");
            }

            if (passengerRepository.existsByEmail(request.email())) {
                throw new BusinessException("Ya existe otro pasajero con el email: " + request.email());
            }
        }

        // Validar perfil si se proporciona
        if (request.profile() != null) {
            validatePassengerProfile(request.profile());
        }
    }

    private void validatePassengerProfile(PassengerDTO.PassengerProfileDTO profile) {
        // Validar teléfono si está presente
        if (profile.phone() != null && !profile.phone().trim().isEmpty()) {
            if (!isValidPhoneNumber(profile.phone())) {
                throw new BusinessException("El formato del teléfono no es válido");
            }
        }

        // Validar código de país si está presente
        if (profile.countryCode() != null && !profile.countryCode().trim().isEmpty()) {
            if (!isValidCountryCode(profile.countryCode())) {
                throw new BusinessException("El código de país no es válido");
            }
        }
    }

    private boolean hasActiveBookings(Passenger passenger) {
        // Implementar lógica para verificar si el pasajero tiene reservas activas
        // return bookingRepository.existsByPassengerIdAndStatusNot(passenger.getId(), "CANCELLED");
        return false;
    }

    // Métodos de utilidad para validaciones
    private boolean isValidEmail(String email) {
        // Implementación básica de validación de email
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhoneNumber(String phone) {
        // Validación básica de número de teléfono
        return phone != null && phone.matches("^[+]?[0-9]{10,15}$");
    }

    private boolean isValidCountryCode(String countryCode) {
        // Validación básica de código de país (ej: +1, +34, +52)
        return countryCode != null && countryCode.matches("^[+][0-9]{1,3}$");
    }
}