package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.FlightDTO;
import com.example.primerapruebaweb.entity.Flight;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.FlightRepository;
import com.example.primerapruebaweb.services.mapper.FlightMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;


    @Override
    @Transactional
    public FlightDTO.FlightResponse create(FlightDTO.FlightRequest request) {
        Flight flight = flightMapper.toEntity(request);
        Flight flightSaved = flightRepository.save(flight);
        return flightMapper.toResponse(flightSaved);
    }

    @Override
    @Transactional
    public FlightDTO.FlightResponse update(Long id, FlightDTO.FlightUpdateRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vuelo " + id + " no actualizado(no se pudo encontrar)"));

        flightMapper.updateEntity(flight, request);
        Flight updatedFlight = flightRepository.save(flight);
        return flightMapper.toResponse(updatedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightDTO.FlightResponse findById(Long id) {
        return flightRepository.findById(id)
                .map(flightMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vuelo  " + id + " no encontrado." ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDTO.FlightResponse> findAll() {
        return flightRepository.findAll()
                .stream()
                .map(flightMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!flightRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontramos el vuelo " + id);
        }
        flightRepository.deleteById(id);
    }
}
