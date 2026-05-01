package com.aiblog.common;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RequestLoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(RequestLoggingAspect.class);

  @Around("@within(org.springframework.web.bind.annotation.RestController)")
  public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes == null ? null : attributes.getRequest();
    long start = System.currentTimeMillis();

    try {
      Object result = joinPoint.proceed();
      if (request != null) {
        log.info("api request completed method={} uri={} handler={} costMs={}",
            request.getMethod(),
            request.getRequestURI(),
            joinPoint.getSignature().toShortString(),
            System.currentTimeMillis() - start);
      }
      return result;
    } catch (Throwable throwable) {
      if (request != null) {
        log.warn("api request failed method={} uri={} handler={} costMs={} error={}",
            request.getMethod(),
            request.getRequestURI(),
            joinPoint.getSignature().toShortString(),
            System.currentTimeMillis() - start,
            throwable.getMessage());
      }
      throw throwable;
    }
  }
}
