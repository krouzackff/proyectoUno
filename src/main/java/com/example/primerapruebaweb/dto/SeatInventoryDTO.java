package com.example.primerapruebaweb.dto;

import com.example.primerapruebaweb.utilities.Cabin;

import java.io.Serializable;

public class SeatInventoryDTO {
    public record SeatInventoryRequest(Cabin cabin, int totalSeats, int availableSeats, Long flightId) implements Serializable{}
    public record SeatInventoryResponse(Long id, Cabin cabin, int totalSeats, int availableSeats, Long flightId) implements Serializable{}
    public record SeatInventoryUpdateRequest(Cabin cabin, int totalSeats, int availableSeats, Long flightId) implements Serializable{}

}
