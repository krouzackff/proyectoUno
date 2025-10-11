package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.BookingItemDTO;
import com.example.primerapruebaweb.entity.Booking;
import com.example.primerapruebaweb.entity.BookingItem;
import com.example.primerapruebaweb.entity.Flight;
import com.example.primerapruebaweb.repository.BookingRepository;
import com.example.primerapruebaweb.repository.FlightRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.BookingItemRepository;
import com.example.primerapruebaweb.services.mapper.BookingItemMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingItemServiceImpl implements BookingItemService {

    private final BookingItemRepository bookingItemRepository;
    private final BookingItemMapper bookingItemMapper;
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingItemDTO.BookingItemResponse create(BookingItemDTO.BookingItemRequest request) {
        // 1. Validar y obtener entidades completas (con repositorios CORRECTOS)
        Booking booking = bookingRepository.findById(request.bookingId())  // ✅ bookingRepository
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Booking not found with id: " + request.bookingId()));

        Flight flight = flightRepository.findById(request.flightId())      // ✅ flightRepository
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Flight not found with id: " + request.flightId()));

        // 2. Crear entity manualmente (NO usar mapper)
        BookingItem bookingItem = BookingItem.builder()
                .cabin(request.cabin())
                .price(request.price())
                .segmentOrder(request.segmentOrder())
                .booking(booking)      // entidad completa
                .flight(flight)        //  entidad completa
                .build();

        // 3. Guardar y retornar (NO redeclarar bookingItem)
        BookingItem savedBookingItem = bookingItemRepository.save(bookingItem);  // nombre diferente
        return bookingItemMapper.toResponse(savedBookingItem);
    }

    @Override
    public BookingItemDTO.BookingItemResponse update(Long id, BookingItemDTO.BookingItemUpdateRequest request) {
        BookingItem bookingItem = bookingItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookingItem not found with id " + id));

        bookingItemMapper.updateEntity(bookingItem, request);
        BookingItem updatedBookingItem = bookingItemRepository.save(bookingItem);
        return bookingItemMapper.toResponse(updatedBookingItem);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingItemDTO.BookingItemResponse findById(Long id) {
        return bookingItemRepository.findById(id)
                .map(bookingItemMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookingItem not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingItemDTO.BookingItemResponse> findAll() {
        return bookingItemRepository.findAll()
                .stream()
                .map(bookingItemMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!bookingItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Airport not found");
        }
        bookingItemRepository.deleteById(id);
    }
}
