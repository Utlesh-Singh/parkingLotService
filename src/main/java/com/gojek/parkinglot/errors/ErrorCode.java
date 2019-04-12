package com.gojek.parkinglot.errors;

public abstract class ErrorCode {
    private String errorCode;
    private String message;

    protected ErrorCode(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
