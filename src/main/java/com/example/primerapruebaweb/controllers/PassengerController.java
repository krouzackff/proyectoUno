package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.PassengerDTO;
import com.example.primerapruebaweb.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerDTO.PassengerResponse> create(
            @RequestBody PassengerDTO.PassengerRequest request) {
        PassengerDTO.PassengerResponse response = passengerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO.PassengerResponse> findById(@PathVariable Long id) {
        PassengerDTO.PassengerResponse response = passengerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PassengerDTO.PassengerResponse>> findAll() {
        List<PassengerDTO.PassengerResponse> responses = passengerService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDTO.PassengerResponse> update(
            @PathVariable Long id,
            @RequestBody PassengerDTO.PassengerUpdateRequest request) {
        PassengerDTO.PassengerResponse response = passengerService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}