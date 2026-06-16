package com.proyecto3.crud.exception;

/**
 * Se lanza cuando un recurso solicitado (producto o categoría) no existe.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
