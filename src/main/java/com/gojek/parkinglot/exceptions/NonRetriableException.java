package com.gojek.parkinglot.exceptions;

public class NonRetriableException extends RuntimeException {

    public NonRetriableException(String message) {
        super(message);
    }

    public NonRetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
