package com.emmtrix.isl.core;

public class IslException extends RuntimeException {
  public IslException(String message) {
    super(message);
  }

  public IslException(String message, Throwable cause) {
    super(message, cause);
  }
}
