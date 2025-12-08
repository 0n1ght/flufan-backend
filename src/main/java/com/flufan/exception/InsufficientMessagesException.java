package com.flufan.exception;

public class InsufficientMessagesException extends RuntimeException {
    public InsufficientMessagesException(String message) {
        super(message);
    }
}
