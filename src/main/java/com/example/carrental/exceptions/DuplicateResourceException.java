package com.example.carrental.exceptions;

public class DuplicateResourceException extends UserException {

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
    }
}

