package com.example.carrental.exceptions;

import org.springframework.http.HttpStatus;

public class ImageUploadException extends UserException {

    public ImageUploadException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.name());
    }
}
