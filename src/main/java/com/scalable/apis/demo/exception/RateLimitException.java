package com.scalable.apis.demo.exception;

public class RateLimitException extends Exception {
    public RateLimitException(String message) {
        super(message);
    }
}
