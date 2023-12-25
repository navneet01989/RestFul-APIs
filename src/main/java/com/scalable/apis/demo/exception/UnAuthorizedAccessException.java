package com.scalable.apis.demo.exception;

public class UnAuthorizedAccessException extends Exception {
    public UnAuthorizedAccessException(String message) {
        super(message);
    }
}
