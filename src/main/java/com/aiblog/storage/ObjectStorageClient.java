package com.aiblog.storage;

import java.io.InputStream;

public interface ObjectStorageClient {

  String providerCode();

  StoredObject put(String objectKey, InputStream inputStream, long size, String contentType);
}
