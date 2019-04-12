package com.gojek.parkinglot.exceptions;

public class ElasticIndexException extends RuntimeException {

    public ElasticIndexException(String errorMessage) {
        super(errorMessage);
    }

    public ElasticIndexException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
