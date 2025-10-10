package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.FlightDTO;
import com.example.primerapruebaweb.services.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<FlightDTO.FlightResponse> create(@RequestBody FlightDTO.FlightRequest request) {
        FlightDTO.FlightResponse response = flightService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO.FlightResponse> findById(@PathVariable Long id) {
        FlightDTO.FlightResponse response = flightService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO.FlightResponse>> findAll() {
        List<FlightDTO.FlightResponse> responses = flightService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDTO.FlightResponse> update(
            @PathVariable Long id,
            @RequestBody FlightDTO.FlightUpdateRequest request) {
        FlightDTO.FlightResponse response = flightService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }
}