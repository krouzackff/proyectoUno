package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirportDTO;
import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.repository.AirportRepository;
import com.example.primerapruebaweb.services.mapper.AirportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    @Override
    @Transactional
    public AirportDTO.AirportResponse create(AirportDTO.AirportRequest request) {
        Airport airport = airportMapper.toEntity(request);
        Airport savedAirport = airportRepository.save(airport);
        return airportMapper.toResponse(savedAirport);
    }

    @Override
    @Transactional
    public AirportDTO.AirportResponse update(Long id, AirportDTO.AirportUpdateRequest request) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Airport not found with id: " + id));

        airportMapper.updateEntity(airport, request);
        Airport updatedAirport = airportRepository.save(airport);

        return airportMapper.toResponse(updatedAirport);
    }

    @Override
    @Transactional(readOnly = true)
    public AirportDTO.AirportResponse findById(Long id) {
        return airportRepository.findById(id)
                .map(airportMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Papi no encontramos el aeropuerto con id: " + id + " quiere una caribanola mientras lo arregla?"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirportDTO.AirportResponse> findAll() {
        return airportRepository.findAll()
                .stream()
                .map(airportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mijo ese aeropuerto no existe!");
        }
        airportRepository.deleteById(id);
    }
}