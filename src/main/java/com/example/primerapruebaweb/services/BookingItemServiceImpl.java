package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.BookingItemDTO;
import com.example.primerapruebaweb.entity.BookingItem;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.BookingItemRepository;
import com.example.primerapruebaweb.services.mapper.BookingItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingItemServiceImpl implements BookingItemService {

    private final BookingItemRepository bookingItemRepository;
    private final BookingItemMapper bookingItemMapper;

    @Override
    @Transactional
    public BookingItemDTO.BookingItemResponse create(BookingItemDTO.BookingItemRequest request) {
        // Conversión directa y guardado
        BookingItem bookingItem = bookingItemMapper.toEntity(request);
        BookingItem savedBookingItem = bookingItemRepository.save(bookingItem);
        return bookingItemMapper.toResponse(savedBookingItem);
    }

    @Override
    @Transactional
    public BookingItemDTO.BookingItemResponse update(Long id, BookingItemDTO.BookingItemUpdateRequest request) {
        // Buscar, actualizar y guardar
        BookingItem bookingItem = bookingItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookingItem", id));

        bookingItemMapper.updateEntity(bookingItem, request);
        BookingItem updatedBookingItem = bookingItemRepository.save(bookingItem);

        return bookingItemMapper.toResponse(updatedBookingItem);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingItemDTO.BookingItemResponse findById(Long id) {
        // Búsqueda simple por ID
        return bookingItemRepository.findById(id)
                .map(bookingItemMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("BookingItem", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingItemDTO.BookingItemResponse> findAll() {
        // Lista completa
        return bookingItemRepository.findAll()
                .stream()
                .map(bookingItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Eliminación directa
        BookingItem bookingItem = bookingItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookingItem", id));

        bookingItemRepository.delete(bookingItem);
    }
}