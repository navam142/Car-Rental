package com.example.carrental.exceptions;

public class ResourceNotFoundException extends UserException {

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}

