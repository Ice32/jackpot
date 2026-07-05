package com.sporty.jackpot_service.exception;

public class ProcessingConflictException extends RuntimeException {
    public ProcessingConflictException(String message) {
        super(message);
    }
}
