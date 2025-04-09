package com.frinkan.exception;

public class MessageDoesNotExist extends RuntimeException {
  public MessageDoesNotExist(String message) {
    super(message);
  }
}
