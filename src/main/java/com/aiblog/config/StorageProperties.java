package com.aiblog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(String activeProvider, Minio minio) {

  public record Minio(String endpoint, String accessKey, String secretKey, String bucket, String publicEndpoint) {
  }
}
