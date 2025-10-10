package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.PassengerDTO;
import com.example.primerapruebaweb.entity.Passenger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.PassengerRepository;
import com.example.primerapruebaweb.services.mapper.PassengerMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;


    @Override
    @Transactional
    public PassengerDTO.PassengerResponse create(PassengerDTO.PassengerRequest request) {
        Passenger passenger = passengerMapper.toEntity(request);
        Passenger passengerSaved = passengerRepository.save(passenger);
        return passengerMapper.toResponse(passengerSaved);
    }

    @Override
    @Transactional
    public PassengerDTO.PassengerResponse update(Long id, PassengerDTO.PassengerUpdateRequest request) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado con id " + id));

        passengerMapper.updateEntity(passenger, request);
        Passenger updatedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toResponse(updatedPassenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerDTO.PassengerResponse findById(Long id) {
        return passengerRepository.findById(id)
                .map(passengerMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero " + id + " no encontrado"));
    }

    @Override
    public List<PassengerDTO.PassengerResponse> findAll() {
        return passengerRepository.findAll()
                .stream()
                .map(passengerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!passengerRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pudimos borrar el pasajero, no lo encontramos");
        }
        passengerRepository.deleteById(id);
    }
}
