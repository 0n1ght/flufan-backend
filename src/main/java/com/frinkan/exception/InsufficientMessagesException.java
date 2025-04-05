package com.frinkan.exception;

public class InsufficientMessagesException extends RuntimeException {
    private String message;

    public InsufficientMessagesException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
