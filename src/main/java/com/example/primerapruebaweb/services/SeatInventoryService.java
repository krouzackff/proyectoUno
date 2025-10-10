package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.SeatInventoryDTO;

import java.util.List;

public interface SeatInventoryService {
    SeatInventoryDTO.SeatInventoryResponse create(SeatInventoryDTO.SeatInventoryRequest request);
    SeatInventoryDTO.SeatInventoryResponse update(Long id, SeatInventoryDTO.SeatInventoryUpdateRequest request);
    SeatInventoryDTO.SeatInventoryResponse findById(Long id);
    List<SeatInventoryDTO.SeatInventoryResponse> findAll();
    void delete(Long id);
}
