package com.aiblog.storage;

import com.aiblog.storage.entity.StorageObject;
import com.aiblog.storage.mapper.StorageObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

  private final StorageProviderFactory providerFactory;
  private final StorageObjectMapper storageObjectMapper;

  public StorageService(StorageProviderFactory providerFactory, StorageObjectMapper storageObjectMapper) {
    this.providerFactory = providerFactory;
    this.storageObjectMapper = storageObjectMapper;
  }

  @Transactional(rollbackFor = Exception.class)
  public StorageObject upload(MultipartFile file) {
    String objectKey = "uploads/" + LocalDateTime.now().toLocalDate() + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
    try {
      StoredObject stored = providerFactory.activeClient()
          .put(objectKey, file.getInputStream(), file.getSize(), file.getContentType());
      StorageObject object = new StorageObject();
      object.setProviderCode(stored.providerCode());
      object.setBucketName(stored.bucketName());
      object.setObjectKey(stored.objectKey());
      object.setOriginalName(file.getOriginalFilename());
      object.setContentType(stored.contentType());
      object.setSizeBytes(stored.sizeBytes());
      object.setEtag(stored.etag());
      object.setPublicUrl(stored.publicUrl());
      object.setStatus(1);
      object.setCreatedAt(LocalDateTime.now());
      object.setUpdatedAt(LocalDateTime.now());
      storageObjectMapper.insert(object);
      return object;
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to read upload file", exception);
    }
  }
}
