package com.aiblog.common;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;
  private final int code;
  private final HttpStatus httpStatus;

  public BusinessException(ErrorCode errorCode) {
    this(errorCode, errorCode.message());
  }

  public BusinessException(ErrorCode errorCode, String message) {
    this(errorCode, message, null);
  }

  public BusinessException(ErrorCode errorCode, Throwable cause) {
    this(errorCode, errorCode.message(), cause);
  }

  public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.code = errorCode.code();
    this.httpStatus = httpStatusFor(errorCode);
  }

  public BusinessException(int code, String message) {
    this(code, message, HttpStatus.BAD_REQUEST);
  }

  public BusinessException(int code, String message, Throwable cause) {
    this(code, message, HttpStatus.BAD_REQUEST, cause);
  }

  public BusinessException(int code, String message, HttpStatus httpStatus) {
    this(code, message, httpStatus, null);
  }

  public BusinessException(int code, String message, HttpStatus httpStatus, Throwable cause) {
    super(message, cause);
    this.errorCode = null;
    this.code = code;
    this.httpStatus = httpStatus == null ? HttpStatus.BAD_REQUEST : httpStatus;
  }

  public ErrorCode errorCode() {
    return errorCode;
  }

  public int getCode() {
    return code;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  private static HttpStatus httpStatusFor(ErrorCode errorCode) {
    return switch (errorCode) {
      case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
      case FORBIDDEN -> HttpStatus.FORBIDDEN;
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case REPEAT_SUBMIT -> HttpStatus.TOO_MANY_REQUESTS;
      case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      default -> HttpStatus.BAD_REQUEST;
    };
  }
}
