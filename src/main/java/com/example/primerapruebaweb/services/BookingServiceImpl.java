package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.BookingDTO;
import com.example.primerapruebaweb.entity.Booking;
import com.example.primerapruebaweb.exception.BusinessException;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.BookingRepository;
import com.example.primerapruebaweb.services.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDTO.BookingResponse create(BookingDTO.BookingRequest request) {
        try {
            log.info("Creando nueva reserva para pasajero ID: {}", request.passengerId());

            // Validaciones de negocio
            validateBookingBusinessRules(request);

            Booking booking = bookingMapper.toEntity(request);
            Booking bookingSaved = bookingRepository.save(booking);

            log.info("Reserva creada exitosamente con ID: {} y número: {}",
                    bookingSaved.getId(), bookingSaved.getBookingNumber());
            return bookingMapper.toResponse(bookingSaved);

        } catch (BusinessException ex) {
            log.warn("Error de negocio al crear reserva: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al crear reserva", ex);
            throw new BusinessException("Error al crear la reserva", ex);
        }
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse update(Long id, BookingDTO.BookingUpdateRequest request) {
        try {
            log.info("Actualizando reserva con ID: {}", id);

            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

            // Validaciones de negocio para actualización
            validateBookingUpdateBusinessRules(booking, request);

            bookingMapper.updateEntity(booking, request);
            Booking updatedBooking = bookingRepository.save(booking);

            log.info("Reserva actualizada exitosamente: {}", updatedBooking.getId());
            return bookingMapper.toResponse(updatedBooking);

        } catch (ResourceNotFoundException ex) {
            log.warn("Reserva no encontrada para actualizar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al actualizar reserva: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al actualizar reserva ID: {}", id, ex);
            throw new BusinessException("Error al actualizar la reserva", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO.BookingResponse findById(Long id) {
        try {
            log.debug("Buscando reserva con ID: {}", id);

            return bookingRepository.findById(id)
                    .map(booking -> {
                        log.debug("Reserva encontrada: {} - Estado: {}",
                                booking.getBookingNumber(), booking.getStatus());
                        return bookingMapper.toResponse(booking);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        } catch (ResourceNotFoundException ex) {
            log.warn("Reserva no encontrada: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al buscar reserva ID: {}", id, ex);
            throw new BusinessException("Error al buscar la reserva", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO.BookingResponse> findAll() {
        try {
            log.debug("Obteniendo todas las reservas");
            List<BookingDTO.BookingResponse> bookings = bookingRepository.findAll()
                    .stream()
                    .map(bookingMapper::toResponse)
                    .toList();

            log.debug("Se encontraron {} reservas", bookings.size());
            return bookings;

        } catch (Exception ex) {
            log.error("Error inesperado al obtener todas las reservas", ex);
            throw new BusinessException("Error al obtener las reservas", ex);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            log.info("Eliminando reserva con ID: {}", id);

            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

            // Validaciones de negocio antes de eliminar
            if (!canCancelBooking(booking)) {
                throw new BusinessException("No se puede eliminar la reserva porque no está en un estado cancelable");
            }

            if (isFlightDeparted(booking)) {
                throw new BusinessException("No se puede eliminar la reserva porque el vuelo ya ha partido");
            }

            bookingRepository.delete(booking);
            log.info("Reserva eliminada exitosamente: {}", id);

        } catch (ResourceNotFoundException ex) {
            log.warn("Reserva no encontrada para eliminar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al eliminar reserva: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al eliminar reserva ID: {}", id, ex);
            throw new BusinessException("Error al eliminar la reserva", ex);
        }
    }

    // Método adicional para cancelar reserva (en lugar de eliminar)
    @Override
    @Transactional
    public BookingDTO.BookingResponse cancel(Long id) {
        try {
            log.info("Cancelando reserva con ID: {}", id);

            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

            if (!canCancelBooking(booking)) {
                throw new BusinessException("No se puede cancelar la reserva en el estado actual: " + booking.getStatus());
            }

            if (isFlightDeparted(booking)) {
                throw new BusinessException("No se puede cancelar la reserva porque el vuelo ya ha partido");
            }

            // Lógica de cancelación específica
            booking.setStatus("CANCELLED");
            Booking cancelledBooking = bookingRepository.save(booking);

            log.info("Reserva cancelada exitosamente: {}", cancelledBooking.getId());
            return bookingMapper.toResponse(cancelledBooking);

        } catch (ResourceNotFoundException ex) {
            log.warn("Reserva no encontrada para cancelar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al cancelar reserva: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al cancelar reserva ID: {}", id, ex);
            throw new BusinessException("Error al cancelar la reserva", ex);
        }
    }

    // Métodos auxiliares para validaciones de negocio
    private void validateBookingBusinessRules(BookingDTO.BookingRequest request) {
        // Validar campos obligatorios
        if (request.passengerId() == null) {
            throw new BusinessException("El pasajero es obligatorio");
        }

        if (request.flightId() == null) {
            throw new BusinessException("El vuelo es obligatorio");
        }

        if (request.seatInventoryId() == null) {
            throw new BusinessException("El inventario de asientos es obligatorio");
        }

        // Validar que el vuelo exista y esté disponible
        if (!isFlightAvailable(request.flightId())) {
            throw new BusinessException("El vuelo seleccionado no está disponible");
        }

        // Validar que haya asientos disponibles
        if (!areSeatsAvailable(request.seatInventoryId(), request.numberOfPassengers())) {
            throw new BusinessException("No hay suficientes asientos disponibles");
        }

        // Validar número de pasajeros
        if (request.numberOfPassengers() <= 0) {
            throw new BusinessException("El número de pasajeros debe ser mayor a 0");
        }

        if (request.numberOfPassengers() > 10) { // Límite razonable
            throw new BusinessException("No se pueden reservar más de 10 pasajeros en una sola reserva");
        }

        // Validar que el pasajero no tenga reservas duplicadas
        if (hasDuplicateBooking(request.passengerId(), request.flightId())) {
            throw new BusinessException("El pasajero ya tiene una reserva para este vuelo");
        }
    }

    private void validateBookingUpdateBusinessRules(Booking booking, BookingDTO.BookingUpdateRequest request) {
        // Validar que no se pueda modificar una reserva cancelada
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BusinessException("No se puede modificar una reserva cancelada");
        }

        // Validar que no se pueda modificar una reserva de un vuelo que ya partió
        if (isFlightDeparted(booking)) {
            throw new BusinessException("No se puede modificar la reserva de un vuelo que ya ha partido");
        }

        // Validar cambios en el número de pasajeros
        if (request.numberOfPassengers() != null && request.numberOfPassengers() != booking.getNumberOfPassengers()) {
            if (request.numberOfPassengers() <= 0) {
                throw new BusinessException("El número de pasajeros debe ser mayor a 0");
            }

            if (!areSeatsAvailable(booking.getSeatInventory().getId(), request.numberOfPassengers())) {
                throw new BusinessException("No hay suficientes asientos disponibles para la nueva cantidad de pasajeros");
            }
        }

        // Validar cambios de estado
        if (request.status() != null && !isValidStatusTransition(booking.getStatus(), request.status())) {
            throw new BusinessException("Transición de estado no permitida: " + booking.getStatus() + " -> " + request.status());
        }
    }

    private boolean canCancelBooking(Booking booking) {
        // Estados en los que se puede cancelar
        return "PENDING".equals(booking.getStatus()) ||
                "CONFIRMED".equals(booking.getStatus()) ||
                "WAITING".equals(booking.getStatus());
    }

    private boolean isFlightDeparted(Booking booking) {
        // Implementar lógica para verificar si el vuelo ya partió
        // return booking.getFlight().getDepartureTime().isBefore(OffsetDateTime.now());
        return false;
    }

    private boolean isFlightAvailable(Long flightId) {
        // Implementar lógica para verificar disponibilidad del vuelo
        // return flightRepository.existsByIdAndStatus(flightId, "ACTIVE");
        return true;
    }

    private boolean areSeatsAvailable(Long seatInventoryId, int numberOfPassengers) {
        // Implementar lógica para verificar disponibilidad de asientos
        // SeatInventory seatInventory = seatInventoryRepository.findById(seatInventoryId).orElse(null);
        // return seatInventory != null && seatInventory.getAvailableSeats() >= numberOfPassengers;
        return true;
    }

    private boolean hasDuplicateBooking(Long passengerId, Long flightId) {
        // Implementar lógica para verificar reservas duplicadas
        // return bookingRepository.existsByPassengerIdAndFlightIdAndStatusNot(passengerId, flightId, "CANCELLED");
        return false;
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Definir transiciones de estado válidas
        return switch (currentStatus) {
            case "PENDING" -> "CONFIRMED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "CONFIRMED" -> "CHECKED_IN".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "CHECKED_IN" -> "BOARDED".equals(newStatus);
            case "WAITING" -> "CONFIRMED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "BOARDED" -> false; // No se puede cambiar después de abordar
            case "CANCELLED" -> false; // No se puede cambiar después de cancelar
            default -> false;
        };
    }
}