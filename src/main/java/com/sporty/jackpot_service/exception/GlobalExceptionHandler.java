package com.sporty.jackpot_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProcessingConflictException.class)
    public ResponseEntity<Map<String, String>> handleProcessingConflict(ProcessingConflictException ex) {
        // HTTP 409 Conflict tells the frontend that the resource state isn't ready yet
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
