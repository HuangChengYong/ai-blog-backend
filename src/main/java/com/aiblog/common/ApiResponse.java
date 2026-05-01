package com.aiblog.common;

import java.time.Instant;
import org.slf4j.MDC;

public record ApiResponse<T>(int code, String message, T data, String traceId, long timestamp) {

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(ErrorCode.OK.code(), ErrorCode.OK.message(), data, currentTraceId(), Instant.now().toEpochMilli());
  }

  public static ApiResponse<Void> ok() {
    return new ApiResponse<>(ErrorCode.OK.code(), ErrorCode.OK.message(), null, currentTraceId(), Instant.now().toEpochMilli());
  }

  public static ApiResponse<Void> error(int code, String message) {
    return new ApiResponse<>(code, message, null, currentTraceId(), Instant.now().toEpochMilli());
  }

  public static ApiResponse<Void> error(ErrorCode errorCode) {
    return error(errorCode.code(), errorCode.message());
  }

  private static String currentTraceId() {
    return MDC.get("traceId");
  }
}
