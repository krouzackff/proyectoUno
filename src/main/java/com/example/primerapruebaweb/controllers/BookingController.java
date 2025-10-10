package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.BookingDTO;
import com.example.primerapruebaweb.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO.BookingResponse> create(@RequestBody BookingDTO.BookingRequest request) {
        BookingDTO.BookingResponse response = bookingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO.BookingResponse> findById(@PathVariable Long id) {
        BookingDTO.BookingResponse response = bookingService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO.BookingResponse>> findAll() {
        List<BookingDTO.BookingResponse> responses = bookingService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO.BookingResponse> update(
            @PathVariable Long id,
            @RequestBody BookingDTO.BookingUpdateRequest request) {
        BookingDTO.BookingResponse response = bookingService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}