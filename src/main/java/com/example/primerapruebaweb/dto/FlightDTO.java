package com.example.primerapruebaweb.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class FlightDTO {

    public record FlightRequest(
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            Long airlineId,
            Long originId,
            Long destinationId,
            Set<TagDTO.TagRequest> tags,
            List<SeatInventoryDTO.SeatInventoryRequest> seatInventories
    ) implements Serializable {}

    public record FlightResponse(
            Long id,
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            AirlineDTO.AirlineResponse airline,
            AirportDTO.AirportResponse origin,
            AirportDTO.AirportResponse destination,
            Set<TagDTO.TagResponse> tags,
            List<SeatInventoryDTO.SeatInventoryResponse> seatInventories
    ) implements Serializable {}

    // Para actualización - campos opcionales
    public record FlightUpdateRequest(
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            Long airlineId,
            Long originId,
            Long destinationId,
            Set<TagDTO.TagUpdateRequest> tags,
            List<SeatInventoryDTO.SeatInventoryUpdateRequest> seatInventories
    ) implements Serializable {}
}