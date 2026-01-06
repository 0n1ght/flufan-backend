package com.flufan.common.exception;

public class AuthenticatedAccountNotFoundException extends RuntimeException {
    public AuthenticatedAccountNotFoundException() {
        super("Logged in user not found");
    }

    public AuthenticatedAccountNotFoundException(String message) {
        super(message);
    }
}
