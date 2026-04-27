package com.example.carrental.exceptions;

public class VerificationTokenException extends UserException {

    public VerificationTokenException(String message) {
        super(message, "VERIFICATION_TOKEN_ERROR");
    }
}

