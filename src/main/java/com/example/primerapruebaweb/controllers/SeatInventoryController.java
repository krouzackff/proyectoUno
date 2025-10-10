package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.SeatInventoryDTO;
import com.example.primerapruebaweb.services.SeatInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-inventories")
@RequiredArgsConstructor
public class SeatInventoryController {

    private final SeatInventoryService seatInventoryService;

    @PostMapping
    public ResponseEntity<SeatInventoryDTO.SeatInventoryResponse> create(@RequestBody SeatInventoryDTO.SeatInventoryRequest request) {
        SeatInventoryDTO.SeatInventoryResponse response = seatInventoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatInventoryDTO.SeatInventoryResponse> findById(@PathVariable Long id) {
        SeatInventoryDTO.SeatInventoryResponse response = seatInventoryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SeatInventoryDTO.SeatInventoryResponse>> findAll() {
        List<SeatInventoryDTO.SeatInventoryResponse> responses = seatInventoryService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatInventoryDTO.SeatInventoryResponse> update(
            @PathVariable Long id,
            @RequestBody SeatInventoryDTO.SeatInventoryUpdateRequest request) {
        SeatInventoryDTO.SeatInventoryResponse response = seatInventoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatInventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}