package com.example.carrental.exceptions;

public abstract class UserException extends RuntimeException {

    private final String errorCode;

    protected UserException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

