package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.BookingDTO;
import com.example.primerapruebaweb.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.BookingRepository;
import com.example.primerapruebaweb.services.mapper.BookingMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;


    @Override
    @Transactional
    public BookingDTO.BookingResponse create(BookingDTO.BookingRequest request) {
        Booking booking = bookingMapper.toEntity(request);
        Booking bookingSaved = bookingRepository.save(booking);
        return bookingMapper.toResponse(bookingSaved);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse update(Long id, BookingDTO.BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with id " + id));

        bookingMapper.updateEntity(booking, request);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO.BookingResponse findById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO.BookingResponse> findAll() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la agenda");
        }
        bookingRepository.deleteById(id);
    }
}
