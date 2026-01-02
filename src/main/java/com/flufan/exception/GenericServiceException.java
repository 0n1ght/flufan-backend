package com.flufan.exception;

public class GenericServiceException extends RuntimeException {
    public GenericServiceException() {
        super("An error occurred. Please try again later");
    }

    public GenericServiceException(String message) {
        super(message);
    }
}
