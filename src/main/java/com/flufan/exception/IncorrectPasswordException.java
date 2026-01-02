package com.flufan.exception;

public class IncorrectPasswordException extends RuntimeException {
  public IncorrectPasswordException() {
    super("Incorrect password");
  }

  public IncorrectPasswordException(String message) {
    super(message);
  }
}
