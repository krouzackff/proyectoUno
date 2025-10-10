package com.example.primerapruebaweb.dto;

import java.io.Serializable;

public class AirlineDTO {
    public record AirlineRequest(String code, String name) implements Serializable{}
    public record AirlineResponse(Long id, String code, String name) implements Serializable{}
    public record AirlineUpdateRequest(String code, String name) implements Serializable{}
}
