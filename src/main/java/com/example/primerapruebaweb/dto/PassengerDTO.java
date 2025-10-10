package com.example.primerapruebaweb.dto;



import java.io.Serializable;

public class PassengerDTO {
    public record PassengerProfileDTO(
            String phone,
            String countryCode
    ) implements Serializable {}

    public record PassengerRequest(String fullname, String email, PassengerProfileDTO profile) implements Serializable{}
    public record PassengerResponse(Long id, String fullname, String email, PassengerProfileDTO profile) implements Serializable{}
    public record PassengerUpdateRequest(String fullname, String email, PassengerProfileDTO profile) implements Serializable{}


}
