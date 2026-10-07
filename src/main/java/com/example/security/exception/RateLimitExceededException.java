package com.example.security.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("Too many requests. Try again later.");
    }
}
