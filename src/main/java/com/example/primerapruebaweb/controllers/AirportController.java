package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.AirportDTO;
import com.example.primerapruebaweb.services.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @PostMapping
    public ResponseEntity<AirportDTO.AirportResponse> create(@RequestBody AirportDTO.AirportRequest request) {
        AirportDTO.AirportResponse response = airportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportDTO.AirportResponse> findById(@PathVariable Long id) {
        AirportDTO.AirportResponse response = airportService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AirportDTO.AirportResponse>> findAll() {
        List<AirportDTO.AirportResponse> responses = airportService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportDTO.AirportResponse> update(
            @PathVariable Long id,
            @RequestBody AirportDTO.AirportUpdateRequest request) {
        AirportDTO.AirportResponse response = airportService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}