package com.example.primerapruebaweb.services;


import com.example.primerapruebaweb.dto.AirportDTO;

import java.util.List;

public interface AirportService {
    AirportDTO.AirportResponse create(AirportDTO.AirportRequest request);
    AirportDTO.AirportResponse update(Long id, AirportDTO.AirportUpdateRequest request);
    AirportDTO.AirportResponse findById(Long id);
    List<AirportDTO.AirportResponse> findAll();
    void delete(Long id);
}
