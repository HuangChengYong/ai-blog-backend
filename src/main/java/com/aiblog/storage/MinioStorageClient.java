package com.aiblog.storage;

import com.aiblog.config.StorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import java.io.InputStream;
import org.springframework.stereotype.Component;

@Component
public class MinioStorageClient implements ObjectStorageClient {

  private final StorageProperties properties;
  private final MinioClient minioClient;

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
      String publicUrl = minio.publicEndpoint() + "/" + minio.bucket() + "/" + objectKey;
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
    boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    if (!exists) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }
  }
}
