package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.FlightDTO;
import java.util.List;

public interface FlightService {
    FlightDTO.FlightResponse create(FlightDTO.FlightRequest request);
    FlightDTO.FlightResponse update(Long id, FlightDTO.FlightUpdateRequest request);
    FlightDTO.FlightResponse findById(Long id);
    List<FlightDTO.FlightResponse> findAll();
    void delete(Long id);
}
