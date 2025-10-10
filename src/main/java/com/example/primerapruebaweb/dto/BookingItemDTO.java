package com.example.primerapruebaweb.dto;

import com.example.primerapruebaweb.utilities.Cabin;

import java.io.Serializable;
import java.math.BigDecimal;

public class BookingItemDTO {
    public record BookingItemRequest(Cabin cabin, BigDecimal price, int segmentOrder, Long bookingId, Long flightId) implements Serializable{}
    public record BookingItemResponse(Long id, Cabin cabin, BigDecimal price, int segmentOrder, BookingDTO.BookingResponse booking, FlightDTO.FlightResponse flight) implements Serializable{}
    public record BookingItemUpdateRequest(Cabin cabin, BigDecimal price, int segmentOrder, Long bookingId, Long flightId) implements Serializable{}

}