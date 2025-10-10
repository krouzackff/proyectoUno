package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.services.mapper.AirportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.AirlineRepository;
import com.example.primerapruebaweb.services.mapper.AirlineMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;
    private final AirportMapper airportMapper;

    //se van a ir haciendo los metodos que contenga el repositorio

    @Override
    @Transactional
    public AirlineDTO.AirlineResponse create(AirlineDTO.AirlineRequest request){
        Airline airline = airlineMapper.toEntity(request);
        Airline savedAirline = airlineRepository.save(airline);
        return airlineMapper.toResponse(savedAirline);
    }

    @Override
    @Transactional
    public AirlineDTO.AirlineResponse update(Long id, AirlineDTO.AirlineUpdateRequest request){
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Airline not found with with id " + id));
        airlineMapper.updateEntity(airline, request);
        Airline updatedAirline = airlineRepository.save(airline);
        return airlineMapper.toResponse(updatedAirline);

    }

    @Override
    @Transactional(readOnly = true)
    public AirlineDTO.AirlineResponse findById(Long id){
        return airlineRepository.findById(id)
                .map(airlineMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Airline not found with id " + id));

    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineDTO.AirlineResponse> findAll(){
        return airlineRepository.findAll().stream().map(airlineMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id){
        if (!airlineRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mani no pudimos borrar la aerolinea porque no la encontramos");
        }
        airlineRepository.deleteById(id);
    }
}
