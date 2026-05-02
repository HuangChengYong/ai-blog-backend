package com.aiblog.common;

public enum ErrorCode {
  OK(0, "ok"),
  BAD_REQUEST(400, "请求参数错误"),
  UNAUTHORIZED(401, "请先登录"),
  FORBIDDEN(403, "没有操作权限"),
  NOT_FOUND(404, "资源不存在"),
  REPEAT_SUBMIT(429, "请勿重复提交"),
  INTERNAL_ERROR(500, "系统繁忙，请稍后再试"),

  // 新增：文件相关错误枚举
  FILE_EMPTY(4001, "上传文件不能为空"),
  FILE_TYPE_INVALID(4002, "非法的文件类型，仅支持 JPG/PNG/GIF/WEBP"),
  FILE_SIZE_EXCEEDED(4003, "文件大小超出限制 (最大5MB)");

  private final int code;
  private final String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int code() {
    return code;
  }

  public String message() {
    return message;
  }
}
