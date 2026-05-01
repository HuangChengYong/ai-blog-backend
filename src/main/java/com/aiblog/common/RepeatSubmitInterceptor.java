package com.aiblog.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(RepeatSubmitInterceptor.class);

  private final StringRedisTemplate redisTemplate;

  public RepeatSubmitInterceptor(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod) || isReadOnly(request)) {
      return true;
    }

    RepeatSubmit repeatSubmit = handlerMethod.getMethodAnnotation(RepeatSubmit.class);
    if (repeatSubmit == null) {
      repeatSubmit = handlerMethod.getBeanType().getAnnotation(RepeatSubmit.class);
    }
    if (repeatSubmit == null) {
      return true;
    }

    String key = "repeat-submit:" + principalKey(request) + ":" + request.getMethod() + ":" + request.getRequestURI()
        + ":" + request.getHeader("X-Idempotency-Key");
    try {
      Boolean accepted = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(repeatSubmit.seconds()));
      if (Boolean.FALSE.equals(accepted)) {
        throw new BusinessException(ErrorCode.REPEAT_SUBMIT, repeatSubmit.message());
      }
    } catch (BusinessException exception) {
      throw exception;
    } catch (RuntimeException exception) {
      log.warn("repeat submit check skipped because redis is unavailable: {}", exception.getMessage());
    }
    return true;
  }

  private boolean isReadOnly(HttpServletRequest request) {
    String method = request.getMethod().toUpperCase(Locale.ROOT);
    return "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
  }

  private String principalKey(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return authentication.getName();
    }
    String forwardedFor = request.getHeader("X-Forwarded-For");
    return forwardedFor == null || forwardedFor.isBlank() ? request.getRemoteAddr() : forwardedFor.split(",")[0].trim();
  }
}
