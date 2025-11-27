package org.yaojiu.supermarket.exception;

public class TokenInvalidException extends RuntimeException {
  public TokenInvalidException(String message) {
    super(message);
  }
}
