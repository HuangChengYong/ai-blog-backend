package com.aiblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@MapperScan("com.aiblog.**.mapper")
@SpringBootApplication
@ConfigurationPropertiesScan
public class AiBlogBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(AiBlogBackendApplication.class, args);
  }
}
