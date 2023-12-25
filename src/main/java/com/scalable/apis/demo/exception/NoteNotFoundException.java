package com.scalable.apis.demo.exception;

public class NoteNotFoundException extends Exception {
    public NoteNotFoundException(String message) {
        super(message);
    }
}
