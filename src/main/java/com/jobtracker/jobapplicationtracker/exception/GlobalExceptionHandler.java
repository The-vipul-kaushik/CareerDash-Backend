package com.jobtracker.jobapplicationtracker.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jobtracker.jobapplicationtracker.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(IllegalArgumentException ex) {
        ApiResponse<String> response = new ApiResponse<>(null, ex.getMessage(), 404);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<String>> handleForbidden(SecurityException ex) {
        ApiResponse<String> response = new ApiResponse<>(null, ex.getMessage(), 403);
        return ResponseEntity.status(403).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>(null, "Internal server error", 500);
        return ResponseEntity.status(500).body(response);
    }
}