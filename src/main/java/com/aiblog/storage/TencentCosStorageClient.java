package com.aiblog.storage;

import java.io.InputStream;

public class TencentCosStorageClient implements ObjectStorageClient {

  @Override
  public String providerCode() {
    return "tencent-cos";
  }

  @Override
  public StoredObject put(String objectKey, InputStream inputStream, long size, String contentType) {
    throw new UnsupportedOperationException("Add Tencent COS SDK and implement this adapter when COS is selected.");
  }

  @Override
  public void delete(String bucketName, String objectKey) {
    throw new UnsupportedOperationException("Add Tencent COS SDK and implement this adapter when COS is selected.");
  }
}
