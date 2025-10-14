package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.FlightDTO;
import com.example.primerapruebaweb.entity.Flight;
import com.example.primerapruebaweb.exception.BusinessException;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.FlightRepository;
import com.example.primerapruebaweb.services.mapper.FlightMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Override
    @Transactional
    public FlightDTO.FlightResponse create(FlightDTO.FlightRequest request) {
        try {
            log.info("Creando nuevo vuelo: {}", request.number());

            // Validaciones de negocio
            validateFlightBusinessRules(request);

            Flight flight = flightMapper.toEntity(request);
            Flight flightSaved = flightRepository.save(flight);

            log.info("Vuelo creado exitosamente con ID: {}", flightSaved.getId());
            return flightMapper.toResponse(flightSaved);

        } catch (BusinessException ex) {
            log.warn("Error de negocio al crear vuelo: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al crear vuelo", ex);
            throw new BusinessException("Error al crear el vuelo", ex);
        }
    }

    @Override
    @Transactional
    public FlightDTO.FlightResponse update(Long id, FlightDTO.FlightUpdateRequest request) {
        try {
            log.info("Actualizando vuelo con ID: {}", id);

            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vuelo", id));

            // Validaciones de negocio para actualización
            validateFlightUpdateBusinessRules(flight, request);

            flightMapper.updateEntity(flight, request);
            Flight updatedFlight = flightRepository.save(flight);

            log.info("Vuelo actualizado exitosamente: {}", updatedFlight.getId());
            return flightMapper.toResponse(updatedFlight);

        } catch (ResourceNotFoundException ex) {
            log.warn("Vuelo no encontrado para actualizar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al actualizar vuelo: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al actualizar vuelo ID: {}", id, ex);
            throw new BusinessException("Error al actualizar el vuelo", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FlightDTO.FlightResponse findById(Long id) {
        try {
            log.debug("Buscando vuelo con ID: {}", id);

            return flightRepository.findById(id)
                    .map(flight -> {
                        log.debug("Vuelo encontrado: {} - {} a {}",
                                flight.getNumber(),
                                flight.getOrigin().getCode(),
                                flight.getDestination().getCode());
                        return flightMapper.toResponse(flight);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Vuelo", id));

        } catch (ResourceNotFoundException ex) {
            log.warn("Vuelo no encontrado: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al buscar vuelo ID: {}", id, ex);
            throw new BusinessException("Error al buscar el vuelo", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDTO.FlightResponse> findAll() {
        try {
            log.debug("Obteniendo todos los vuelos");
            List<FlightDTO.FlightResponse> flights = flightRepository.findAll()
                    .stream()
                    .map(flightMapper::toResponse)
                    .toList();

            log.debug("Se encontraron {} vuelos", flights.size());
            return flights;

        } catch (Exception ex) {
            log.error("Error inesperado al obtener todos los vuelos", ex);
            throw new BusinessException("Error al obtener los vuelos", ex);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            log.info("Eliminando vuelo con ID: {}", id);

            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vuelo", id));

            // Validaciones de negocio antes de eliminar
            if (hasActiveBookings(flight)) {
                throw new BusinessException("No se puede eliminar el vuelo porque tiene reservas activas");
            }

            if (hasFutureDeparture(flight)) {
                throw new BusinessException("No se puede eliminar el vuelo porque tiene una fecha de salida futura");
            }

            flightRepository.delete(flight);
            log.info("Vuelo eliminado exitosamente: {}", id);

        } catch (ResourceNotFoundException ex) {
            log.warn("Vuelo no encontrado para eliminar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al eliminar vuelo: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al eliminar vuelo ID: {}", id, ex);
            throw new BusinessException("Error al eliminar el vuelo", ex);
        }
    }

    // Métodos auxiliares para validaciones de negocio
    private void validateFlightBusinessRules(FlightDTO.FlightRequest request) {
        // Validar campos obligatorios
        if (request.number() == null || request.number().trim().isEmpty()) {
            throw new BusinessException("El número de vuelo es obligatorio");
        }

        if (request.departureTime() == null) {
            throw new BusinessException("La hora de salida es obligatoria");
        }

        if (request.arrivalTime() == null) {
            throw new BusinessException("La hora de llegada es obligatoria");
        }

        if (request.airlineId() == null) {
            throw new BusinessException("La aerolínea es obligatoria");
        }

        if (request.originId() == null) {
            throw new BusinessException("El aeropuerto de origen es obligatorio");
        }

        if (request.destinationId() == null) {
            throw new BusinessException("El aeropuerto de destino es obligatorio");
        }

        // Validar formato del número de vuelo
        if (!isValidFlightNumber(request.number())) {
            throw new BusinessException("El formato del número de vuelo no es válido");
        }

        // Validar que no exista un vuelo con el mismo número
        if (flightRepository.existsByNumber(request.number())) {
            throw new BusinessException("Ya existe un vuelo con el número: " + request.number());
        }

        // Validar fechas
        validateFlightDates(request.departureTime(), request.arrivalTime());

        // Validar que origen y destino sean diferentes
        if (request.originId().equals(request.destinationId())) {
            throw new BusinessException("El aeropuerto de origen y destino no pueden ser el mismo");
        }
    }

    private void validateFlightUpdateBusinessRules(Flight flight, FlightDTO.FlightUpdateRequest request) {
        // Validar número de vuelo si se proporciona y es diferente al actual
        if (request.number() != null && !request.number().equals(flight.getNumber())) {
            if (!isValidFlightNumber(request.number())) {
                throw new BusinessException("El formato del número de vuelo no es válido");
            }

            if (flightRepository.existsByNumber(request.number())) {
                throw new BusinessException("Ya existe otro vuelo con el número: " + request.number());
            }
        }

        // Validar fechas si se proporcionan
        OffsetDateTime departureTime = request.departureTime() != null ? request.departureTime() : flight.getDepartureTime();
        OffsetDateTime arrivalTime = request.arrivalTime() != null ? request.arrivalTime() : flight.getArrivalTime();

        validateFlightDates(departureTime, arrivalTime);

        // Validar lógica de negocio adicional
        if (hasActiveBookings(flight) && (request.departureTime() != null || request.arrivalTime() != null)) {
            throw new BusinessException("No se puede modificar la hora de vuelos con reservas activas");
        }
    }

    private void validateFlightDates(OffsetDateTime departureTime, OffsetDateTime arrivalTime) {
        // Validar que la hora de llegada sea posterior a la de salida
        if (arrivalTime.isBefore(departureTime) || arrivalTime.isEqual(departureTime)) {
            throw new BusinessException("La hora de llegada debe ser posterior a la hora de salida");
        }

        // Validar que el vuelo no sea en el pasado (para creación)
        if (departureTime.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("No se pueden crear vuelos en el pasado");
        }

        // Validar duración mínima del vuelo (ej: al menos 30 minutos)
        long durationInMinutes = java.time.Duration.between(departureTime, arrivalTime).toMinutes();
        if (durationInMinutes < 30) {
            throw new BusinessException("La duración del vuelo debe ser de al menos 30 minutos");
        }

        // Validar duración máxima del vuelo (ej: no más de 24 horas)
        if (durationInMinutes > 24 * 60) {
            throw new BusinessException("La duración del vuelo no puede exceder las 24 horas");
        }
    }

    private boolean hasActiveBookings(Flight flight) {
        // Implementar lógica para verificar si el vuelo tiene reservas activas
        // return bookingRepository.existsByFlightIdAndStatusNot(flight.getId(), "CANCELLED");
        return false;
    }

    private boolean hasFutureDeparture(Flight flight) {
        // Verificar si el vuelo tiene una fecha de salida futura
        return flight.getDepartureTime().isAfter(OffsetDateTime.now());
    }

    // Métodos de utilidad para validaciones
    private boolean isValidFlightNumber(String flightNumber) {
        // Validación básica de formato de número de vuelo (ej: AA123, BA4567)
        return flightNumber != null && flightNumber.matches("^[A-Z]{2}\\d{3,4}$");
    }
}