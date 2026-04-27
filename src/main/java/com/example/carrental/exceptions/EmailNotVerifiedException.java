package com.example.carrental.exceptions;

public class EmailNotVerifiedException extends UserException {

    public EmailNotVerifiedException(String message) {
        super(message, "EMAIL_NOT_VERIFIED");
    }
}

