package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.BookingItemDTO;
import com.example.primerapruebaweb.services.BookingItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-items")
@RequiredArgsConstructor
public class BookingItemController {

    private final BookingItemService bookingItemService;

    @PostMapping
    public ResponseEntity<BookingItemDTO.BookingItemResponse> create(@RequestBody BookingItemDTO.BookingItemRequest request) {
        BookingItemDTO.BookingItemResponse response = bookingItemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingItemDTO.BookingItemResponse> findById(@PathVariable Long id) {
        BookingItemDTO.BookingItemResponse response = bookingItemService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingItemDTO.BookingItemResponse>> findAll() {
        List<BookingItemDTO.BookingItemResponse> responses = bookingItemService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingItemDTO.BookingItemResponse> update(
            @PathVariable Long id,
            @RequestBody BookingItemDTO.BookingItemUpdateRequest request) {
        BookingItemDTO.BookingItemResponse response = bookingItemService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}