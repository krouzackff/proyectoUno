package com.example.primerapruebaweb.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class BookingDTO {

    public record BookingRequest(OffsetDateTime createdAt, PassengerDTO.PassengerRequest passenger) implements Serializable{}
    public record BookingResponse(Long id, OffsetDateTime createdAt, PassengerDTO.PassengerResponse passenger) implements Serializable{}
    public record BookingUpdateRequest(OffsetDateTime createdAt, PassengerDTO.PassengerUpdateRequest passenger) implements Serializable{}

}
