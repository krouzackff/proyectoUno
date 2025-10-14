package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.AirlineRepository;
import com.example.primerapruebaweb.services.mapper.AirlineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    @Override
    @Transactional
    public AirlineDTO.AirlineResponse create(AirlineDTO.AirlineRequest request) {
        Airline airline = airlineMapper.toEntity(request);
        Airline savedAirline = airlineRepository.save(airline);
        return airlineMapper.toResponse(savedAirline);
    }

    @Override
    @Transactional
    public AirlineDTO.AirlineResponse update(Long id, AirlineDTO.AirlineUpdateRequest request) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airline", id));

        airlineMapper.updateEntity(airline, request);
        Airline updatedAirline = airlineRepository.save(airline);
        return airlineMapper.toResponse(updatedAirline);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineDTO.AirlineResponse findById(Long id) {
        return airlineRepository.findById(id)
                .map(airlineMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Airline", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineDTO.AirlineResponse> findAll() {
        return airlineRepository.findAll()
                .stream()
                .map(airlineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airline", id));
        airlineRepository.delete(airline);
    }
}