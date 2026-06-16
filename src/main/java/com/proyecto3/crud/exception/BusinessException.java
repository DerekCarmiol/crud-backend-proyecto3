package com.proyecto3.crud.exception;

/**
 * Se lanza ante una regla de negocio violada (por ejemplo, borrar una
 * categoría que aún tiene productos asociados).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
