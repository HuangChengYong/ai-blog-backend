package com.aiblog.storage;

import com.aiblog.config.StorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class MinioStorageClient implements ObjectStorageClient {

  private final StorageProperties properties;
  private final MinioClient minioClient;
  private final Set<String> ensuredPublicBuckets = ConcurrentHashMap.newKeySet();

  public MinioStorageClient(StorageProperties properties) {
    this.properties = properties;
    StorageProperties.Minio minio = properties.minio();
    this.minioClient = MinioClient.builder()
        .endpoint(minio.endpoint())
        .credentials(minio.accessKey(), minio.secretKey())
        .build();
  }

  @Override
  public String providerCode() {
    return "minio";
  }

  @Override
  public StoredObject put(String objectKey, InputStream inputStream, long size, String contentType) {
    try {
      StorageProperties.Minio minio = properties.minio();
      ensureBucket(minio.bucket());
      minioClient.putObject(PutObjectArgs.builder()
          .bucket(minio.bucket())
          .object(objectKey)
          .contentType(contentType)
          .stream(inputStream, size, -1)
          .build());
      StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
          .bucket(minio.bucket())
          .object(objectKey)
          .build());
      String publicUrl = publicUrl(minio.publicEndpoint(), minio.bucket(), objectKey);
      return new StoredObject(providerCode(), minio.bucket(), objectKey, contentType, size, stat.etag(), publicUrl);
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to upload object to MinIO", exception);
    }
  }

  @Override
  public void delete(String bucketName, String objectKey) {
    try {
      minioClient.removeObject(RemoveObjectArgs.builder()
          .bucket(bucketName)
          .object(objectKey)
          .build());
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to delete object from MinIO", exception);
    }
  }

  private void ensureBucket(String bucket) throws Exception {
    if (ensuredPublicBuckets.contains(bucket)) {
      return;
    }
    synchronized (ensuredPublicBuckets) {
      if (ensuredPublicBuckets.contains(bucket)) {
        return;
      }
      boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!exists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }
      minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
          .bucket(bucket)
          .config(publicReadPolicy(bucket))
          .build());
      ensuredPublicBuckets.add(bucket);
    }
  }

  private String publicReadPolicy(String bucket) {
    return """
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": {"AWS": ["*"]},
              "Action": ["s3:GetObject"],
              "Resource": ["arn:aws:s3:::%s/*"]
            }
          ]
        }
        """.formatted(bucket);
  }

  private String publicUrl(String endpoint, String bucket, String objectKey) {
    return trimTrailingSlash(endpoint) + "/" + bucket + "/" + encodeObjectKey(objectKey);
  }

  private String trimTrailingSlash(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }
    String trimmed = value.trim();
    while (trimmed.endsWith("/")) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }
    return trimmed;
  }

  private String encodeObjectKey(String objectKey) {
    String[] segments = objectKey.split("/", -1);
    StringBuilder encoded = new StringBuilder();
    for (int i = 0; i < segments.length; i++) {
      if (i > 0) {
        encoded.append('/');
      }
      encoded.append(URLEncoder.encode(segments[i], StandardCharsets.UTF_8).replace("+", "%20"));
    }
    return encoded.toString();
  }
}
