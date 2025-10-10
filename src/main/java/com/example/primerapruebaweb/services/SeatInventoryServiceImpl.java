package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.SeatInventoryDTO;
import com.example.primerapruebaweb.entity.SeatInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.SeatInventoryRepository;
import com.example.primerapruebaweb.services.mapper.SeatInventoryMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SeatInventoryServiceImpl implements SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final SeatInventoryMapper seatInventoryMapper;


    @Override
    @Transactional
    public SeatInventoryDTO.SeatInventoryResponse create(SeatInventoryDTO.SeatInventoryRequest request) {
        SeatInventory seatInventory = seatInventoryMapper.toEntity(request);
        SeatInventory updatedSeatInventory = seatInventoryRepository.save((seatInventory));
        return seatInventoryMapper.toResponse(updatedSeatInventory);
    }

    @Override
    @Transactional
    public SeatInventoryDTO.SeatInventoryResponse update(Long id, SeatInventoryDTO.SeatInventoryUpdateRequest request) {
        SeatInventory seatInventory = seatInventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SeatInventory not found with id " + id));

        seatInventoryMapper.updateEntity(seatInventory, request);
        SeatInventory updatedSeatInventory = seatInventoryRepository.save(seatInventory);
        return seatInventoryMapper.toResponse(updatedSeatInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatInventoryDTO.SeatInventoryResponse findById(Long id) {
        return seatInventoryRepository.findById(id)
                .map(seatInventoryMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "findById(SeatInventory) ha fallado con el id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatInventoryDTO.SeatInventoryResponse> findAll() {
        return seatInventoryRepository.findAll()
                .stream()
                .map(seatInventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(!seatInventoryRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontre el seatInventory a borrar");
        }
        seatInventoryRepository.deleteById(id);
    }
}
