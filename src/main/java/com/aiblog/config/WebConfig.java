package com.aiblog.config;

import com.aiblog.common.RepeatSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final RepeatSubmitInterceptor repeatSubmitInterceptor;

  public WebConfig(RepeatSubmitInterceptor repeatSubmitInterceptor) {
    this.repeatSubmitInterceptor = repeatSubmitInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
  }
}
