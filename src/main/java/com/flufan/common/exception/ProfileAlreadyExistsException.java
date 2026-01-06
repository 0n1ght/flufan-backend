package com.flufan.common.exception;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException() {
        super("Your profile is already created");
    }

    public ProfileAlreadyExistsException(String message) {
        super(message);
    }
}
