package com.flufan.common.exception;

public class TokenExpiredException extends RuntimeException {
  public TokenExpiredException() {
    super("Token expired");
  }

  public TokenExpiredException(String message) {
    super(message);
  }
}
