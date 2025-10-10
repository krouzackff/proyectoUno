package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.BookingItemDTO;
import java.util.List;

public interface BookingItemService {
    BookingItemDTO.BookingItemResponse create(BookingItemDTO.BookingItemRequest request);
    BookingItemDTO.BookingItemResponse update(Long id, BookingItemDTO.BookingItemUpdateRequest request);
    BookingItemDTO.BookingItemResponse findById(Long id);
    List<BookingItemDTO.BookingItemResponse> findAll();
    void delete(Long id);
}
