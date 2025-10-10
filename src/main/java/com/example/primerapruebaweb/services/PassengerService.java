package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.PassengerDTO;
import java.util.List;

public interface PassengerService {
    PassengerDTO.PassengerResponse create(PassengerDTO.PassengerRequest request);
    PassengerDTO.PassengerResponse update(Long id, PassengerDTO.PassengerUpdateRequest request);
    PassengerDTO.PassengerResponse findById(Long id);
    List<PassengerDTO.PassengerResponse> findAll();
    void delete(Long id);
}
