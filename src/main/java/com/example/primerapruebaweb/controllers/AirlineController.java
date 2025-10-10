package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.services.AirlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping
    public ResponseEntity<AirlineDTO.AirlineResponse> create(@RequestBody AirlineDTO.AirlineRequest request) {
        AirlineDTO.AirlineResponse response = airlineService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineDTO.AirlineResponse> findById(@PathVariable Long id) {
        AirlineDTO.AirlineResponse response = airlineService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AirlineDTO.AirlineResponse>> findAll() {
        List<AirlineDTO.AirlineResponse> responses = airlineService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirlineDTO.AirlineResponse> update(
            @PathVariable Long id,
            @RequestBody AirlineDTO.AirlineUpdateRequest request) {
        AirlineDTO.AirlineResponse response = airlineService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airlineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}