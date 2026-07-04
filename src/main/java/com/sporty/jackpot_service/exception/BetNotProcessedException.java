package com.sporty.jackpot_service.exception;

public class BetNotProcessedException extends RuntimeException {
    public BetNotProcessedException(String message) {
        super(message);
    }
}
