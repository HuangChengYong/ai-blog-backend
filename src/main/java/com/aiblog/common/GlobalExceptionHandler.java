package com.aiblog.common;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> business(BusinessException exception) {
    return ApiResponse.error(exception.errorCode().code(), exception.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<Void> badCredentials() {
    return ApiResponse.error(ErrorCode.UNAUTHORIZED.code(), "用户名或密码错误");
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<Void> authentication(AuthenticationException exception) {
    return ApiResponse.error(ErrorCode.UNAUTHORIZED.code(), exception.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiResponse<Void> accessDenied() {
    return ApiResponse.error(ErrorCode.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> validation(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getAllErrors().isEmpty()
        ? ErrorCode.BAD_REQUEST.message()
        : exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return ApiResponse.error(ErrorCode.BAD_REQUEST.code(), message);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> constraint(ConstraintViolationException exception) {
    return ApiResponse.error(ErrorCode.BAD_REQUEST.code(), exception.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> runtime(RuntimeException exception) {
    log.error("Unhandled runtime exception", exception);
    return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> exception(Exception exception) {
    log.error("Unhandled exception", exception);
    return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
  }
}
