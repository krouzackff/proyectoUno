package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.SeatInventoryDTO;
import com.example.primerapruebaweb.entity.SeatInventory;
import com.example.primerapruebaweb.exception.BusinessException;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.SeatInventoryRepository;
import com.example.primerapruebaweb.services.mapper.SeatInventoryMapper;
import com.example.primerapruebaweb.utilities.Cabin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SeatInventoryServiceImpl implements SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final SeatInventoryMapper seatInventoryMapper;

    @Override
    @Transactional
    public SeatInventoryDTO.SeatInventoryResponse create(SeatInventoryDTO.SeatInventoryRequest request) {
        try {
            log.info("Creando nuevo SeatInventory para flightId: {} y cabina: {}", request.flightId(), request.cabin());

            // Validaciones de negocio
            validateSeatInventoryBusinessRules(request);

            SeatInventory seatInventory = seatInventoryMapper.toEntity(request);
            SeatInventory savedSeatInventory = seatInventoryRepository.save(seatInventory);

            log.info("SeatInventory creado exitosamente con ID: {}", savedSeatInventory.getId());
            return seatInventoryMapper.toResponse(savedSeatInventory);

        } catch (BusinessException ex) {
            log.warn("Error de negocio al crear SeatInventory: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al crear SeatInventory", ex);
            throw new BusinessException("Error al crear el inventario de asientos", ex);
        }
    }

    @Override
    @Transactional
    public SeatInventoryDTO.SeatInventoryResponse update(Long id, SeatInventoryDTO.SeatInventoryUpdateRequest request) {
        try {
            log.info("Actualizando SeatInventory con ID: {}", id);

            SeatInventory seatInventory = seatInventoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("SeatInventory", id));

            validateSeatInventoryUpdateBusinessRules(seatInventory, request);
            seatInventoryMapper.updateEntity(seatInventory, request);
            SeatInventory updatedSeatInventory = seatInventoryRepository.save(seatInventory);

            log.info("SeatInventory actualizado exitosamente: {}", updatedSeatInventory.getId());
            return seatInventoryMapper.toResponse(updatedSeatInventory);

        } catch (ResourceNotFoundException ex) {
            log.warn("SeatInventory no encontrado para actualizar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al actualizar SeatInventory: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al actualizar SeatInventory ID: {}", id, ex);
            throw new BusinessException("Error al actualizar el inventario de asientos", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SeatInventoryDTO.SeatInventoryResponse findById(Long id) {
        try {
            log.debug("Buscando SeatInventory con ID: {}", id);

            return seatInventoryRepository.findById(id)
                    .map(seatInventory -> {
                        log.debug("SeatInventory encontrado - Vuelo: {}, Cabina: {}",
                                seatInventory.getFlight().getId(), seatInventory.getCabin());
                        return seatInventoryMapper.toResponse(seatInventory);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("SeatInventory", id));

        } catch (ResourceNotFoundException ex) {
            log.warn("SeatInventory no encontrado: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al buscar SeatInventory ID: {}", id, ex);
            throw new BusinessException("Error al buscar el inventario de asientos", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatInventoryDTO.SeatInventoryResponse> findAll() {
        try {
            log.debug("Obteniendo todos los SeatInventories");
            List<SeatInventoryDTO.SeatInventoryResponse> seatInventories = seatInventoryRepository.findAll()
                    .stream()
                    .map(seatInventoryMapper::toResponse)
                    .toList();

            log.debug("Se encontraron {} SeatInventories", seatInventories.size());
            return seatInventories;

        } catch (Exception ex) {
            log.error("Error inesperado al obtener todos los SeatInventories", ex);
            throw new BusinessException("Error al obtener los inventarios de asientos", ex);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            log.info("Eliminando SeatInventory con ID: {}", id);

            SeatInventory seatInventory = seatInventoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("SeatInventory", id));

            if (hasActiveBookings(seatInventory)) {
                throw new BusinessException("No se puede eliminar el SeatInventory porque tiene reservas activas");
            }

            seatInventoryRepository.delete(seatInventory);
            log.info("SeatInventory eliminado exitosamente: {}", id);

        } catch (ResourceNotFoundException ex) {
            log.warn("SeatInventory no encontrado para eliminar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al eliminar SeatInventory: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al eliminar SeatInventory ID: {}", id, ex);
            throw new BusinessException("Error al eliminar el inventario de asientos", ex);
        }
    }

    // Métodos auxiliares para validaciones de negocio
    private void validateSeatInventoryBusinessRules(SeatInventoryDTO.SeatInventoryRequest request) {
        // Validar que no exista ya un SeatInventory para el mismo vuelo y cabina
        if (seatInventoryRepository.existsByFlightIdAndCabin(request.flightId(), request.cabin())) {
            throw new BusinessException("Ya existe un inventario de asientos para el vuelo " +
                    request.flightId() + " y cabina " + request.cabin());
        }

        // Validar que totalSeats sea mayor que 0
        if (request.totalSeats() <= 0) {
            throw new BusinessException("El número total de asientos debe ser mayor a 0");
        }

        // Validar que availableSeats no sea mayor que totalSeats
        if (request.availableSeats() > request.totalSeats()) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que el total de asientos");
        }

        // Validar que availableSeats no sea negativo
        if (request.availableSeats() < 0) {
            throw new BusinessException("Los asientos disponibles no pueden ser negativos");
        }

        // Validar que la cabina sea válida
        if (request.cabin() == null) {
            throw new BusinessException("La cabina es obligatoria");
        }
    }

    private void validateSeatInventoryUpdateBusinessRules(SeatInventory seatInventory, SeatInventoryDTO.SeatInventoryUpdateRequest request) {
        // Validaciones específicas para actualización

        if (request.totalSeats() <= 0) {
            throw new BusinessException("El número total de asientos debe ser mayor a 0");
        }

        if ( request.availableSeats() < 0) {
            throw new BusinessException("Los asientos disponibles no pueden ser negativos");
        }

        // Validar que al actualizar, los asientos disponibles no superen el total
        if (request.availableSeats() > request.totalSeats()) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que el total de asientos");
        }

        // Validar consistencia si solo se actualiza uno de los campos
        if (request.availableSeats() > seatInventory.getTotalSeats()) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que el total de asientos actual: " +
                    seatInventory.getTotalSeats());
        }

        if (seatInventory.getAvailableSeats() > request.totalSeats()) {
            throw new BusinessException("El nuevo total de asientos no puede ser menor que los asientos disponibles actuales: " +
                    seatInventory.getAvailableSeats());
        }

        // Validar que no se pueda cambiar la cabina a una que ya existe para el mismo vuelo
        if (request.cabin() != null && !request.cabin().equals(seatInventory.getCabin())) {
            if (seatInventoryRepository.existsByFlightIdAndCabin(seatInventory.getFlight().getId(), request.cabin())) {
                throw new BusinessException("Ya existe un inventario de asientos para el vuelo " +
                        seatInventory.getFlight().getId() + " y cabina " + request.cabin());
            }
        }
    }

    private boolean hasActiveBookings(SeatInventory seatInventory) {
        // Implementar lógica para verificar si hay reservas activas para este inventario
        // return bookingRepository.existsBySeatInventoryIdAndStatusNot(seatInventory.getId(), "CANCELLED");
        return false;
    }
}