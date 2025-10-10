package com.example.primerapruebaweb.dto;

import java.io.Serializable;

public class TagDTO {
    public record TagRequest(String name) implements Serializable{}
    public record TagResponse(Long id, String name) implements Serializable{}
    public record TagUpdateRequest(String name) implements Serializable{}
}
