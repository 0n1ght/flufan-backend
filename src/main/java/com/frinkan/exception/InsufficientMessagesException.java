package com.frinkan.exception;

public class InsufficientMessagesException extends RuntimeException {
    public InsufficientMessagesException(String message) {
        super(message);
    }
}
