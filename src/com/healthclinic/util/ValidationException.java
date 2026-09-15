package com.healthclinic.util;

/**
 * Exception thrown when user input or data validation fails.
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}