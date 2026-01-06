package com.flufan.common.exception;

public class MessageDoesNotExist extends RuntimeException {
  public MessageDoesNotExist(String message) {
    super(message);
  }
}
