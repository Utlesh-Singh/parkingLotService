package com.gojek.parkinglot.exceptions;

import com.gojek.parkinglot.errors.ErrorCode;
import org.slf4j.helpers.MessageFormatter;

public class PlatformException extends RuntimeException {
    private ErrorCode errorCode;
    private Throwable cause;
    private Object[] messageParams;

    public PlatformException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public PlatformException(ErrorCode errorCode, Throwable cause) {
        this.errorCode = errorCode;
        this.cause = cause;
    }

    public String getMessage() {
        return this.messageParams != null ? MessageFormatter.arrayFormat(this.errorCode.getMessage(), this.messageParams).getMessage() : this.errorCode.getMessage();
    }

    public String getErrorCode() {
        return this.errorCode.getErrorCode();
    }

    public void setMessageParams(Object... messageParams) {
        this.messageParams = messageParams;
    }

    public Object[] getMessageParams() {
        return this.messageParams;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
