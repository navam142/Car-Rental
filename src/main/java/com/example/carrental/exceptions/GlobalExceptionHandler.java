package com.example.carrental.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiErrorResponse> handleUserException(UserException exception, HttpServletRequest request) {
        HttpStatus status = resolveStatus(exception);
        return ResponseEntity.status(status)
                .body(buildErrorResponse(status, exception.getMessage(), request.getRequestURI(), exception.getErrorCode(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception,
                                                                      HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = buildErrorResponse(
                status,
                "Validation failed",
                request.getRequestURI(),
                "VALIDATION_ERROR",
                fieldErrors
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameNotFound(UsernameNotFoundException exception,
                                                                    HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status)
                .body(buildErrorResponse(status, exception.getMessage(), request.getRequestURI(), "INVALID_CREDENTIALS", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
                                                                  HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(buildErrorResponse(status, exception.getMessage(), request.getRequestURI(), "BAD_REQUEST", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(buildErrorResponse(status, "Unexpected error occurred", request.getRequestURI(), "INTERNAL_ERROR", null));
    }

    private HttpStatus resolveStatus(UserException exception) {
        if (exception instanceof DuplicateResourceException) {
            return HttpStatus.CONFLICT;
        }
        if (exception instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (exception instanceof InvalidCredentialsException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (exception instanceof EmailNotVerifiedException) {
            return HttpStatus.FORBIDDEN;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private ApiErrorResponse buildErrorResponse(HttpStatus status,
                                                String message,
                                                String path,
                                                String code,
                                                Map<String, String> validationErrors) {
        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                code,
                validationErrors
        );
    }
}
