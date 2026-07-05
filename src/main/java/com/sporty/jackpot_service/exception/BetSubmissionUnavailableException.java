package com.sporty.jackpot_service.exception;

public class BetSubmissionUnavailableException extends RuntimeException {
    public BetSubmissionUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
