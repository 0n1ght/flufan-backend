package com.frinkan.exception;

public class MessageLengthException extends RuntimeException {
    private String message;

    public MessageLengthException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
