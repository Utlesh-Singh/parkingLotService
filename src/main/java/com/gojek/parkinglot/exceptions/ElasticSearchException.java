package com.gojek.parkinglot.exceptions;


public class ElasticSearchException extends RuntimeException {

    public ElasticSearchException(String errorMessage) {
        super(errorMessage);
    }

    public ElasticSearchException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
