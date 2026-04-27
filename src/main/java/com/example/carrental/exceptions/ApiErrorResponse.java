package com.example.carrental.exceptions;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String code,
        Map<String, String> validationErrors
) {
}

