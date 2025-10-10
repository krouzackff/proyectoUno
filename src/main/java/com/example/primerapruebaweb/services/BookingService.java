package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.BookingDTO;
import java.util.List;

public interface BookingService {
    BookingDTO.BookingResponse create(BookingDTO.BookingRequest request);
    BookingDTO.BookingResponse update(Long id, BookingDTO.BookingUpdateRequest request);
    BookingDTO.BookingResponse findById(Long id);
    List<BookingDTO.BookingResponse> findAll();
    void delete(Long id);
}
