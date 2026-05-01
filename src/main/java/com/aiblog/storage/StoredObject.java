package com.aiblog.storage;

public record StoredObject(
    String providerCode,
    String bucketName,
    String objectKey,
    String contentType,
    long sizeBytes,
    String etag,
    String publicUrl
) {
}
