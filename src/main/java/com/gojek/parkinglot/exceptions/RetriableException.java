package com.gojek.parkinglot.exceptions;


public class RetriableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RetriableException(String message) {
        super(message);
    }

    public RetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}