package com.example.carrental.exceptions;

public class InvalidRentalOperationException extends UserException {

    public InvalidRentalOperationException(String message) {
        super(message, "INVALID_RENTAL_OPERATION");
    }
}