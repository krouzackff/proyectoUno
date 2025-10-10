package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirlineDTO;
import java.util.List;

public interface AirlineService {
    AirlineDTO.AirlineResponse create(AirlineDTO.AirlineRequest request);
    AirlineDTO.AirlineResponse update(Long id, AirlineDTO.AirlineUpdateRequest request);
    AirlineDTO.AirlineResponse findById(Long id);
    List<AirlineDTO.AirlineResponse> findAll();
    void delete(Long id);
}
