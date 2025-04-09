package com.frinkan.exception;

public class MessageLengthException extends RuntimeException {
    public MessageLengthException(String message) {
        super(message);
    }
}
