package com.example.primerapruebaweb.dto;

import java.io.Serializable;

public class AirportDTO {
    public record AirportRequest(String code, String name, String city) implements Serializable{}
    public record AirportResponse(Long id, String code, String name, String city) implements  Serializable{}
    public record AirportUpdateRequest(String code, String name, String city) implements Serializable{}
}
