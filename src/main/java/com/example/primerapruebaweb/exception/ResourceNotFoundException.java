package com.example.primerapruebaweb.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s no encontrado con id: %d", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(String.format("%s no encontrado con %s: %s", resourceName, field, value));
    }
}