package org.yaojiu.supermarket.exception;

public class NeedLoginExecption extends RuntimeException {
  public NeedLoginExecption(String message) {
    super(message);
  }
}
