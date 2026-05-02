package com.aiblog.storage;

import com.aiblog.storage.entity.StorageObject;
import com.aiblog.storage.entity.StorageObjectRef;
import com.aiblog.storage.mapper.StorageObjectMapper;
import com.aiblog.storage.mapper.StorageObjectRefMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

  private static final String REF_TYPE_ARTICLE = "ARTICLE";
  private static final String USAGE_TYPE_CONTENT_IMAGE = "CONTENT_IMAGE";
  private static final Pattern STORAGE_ID_PATTERN = Pattern.compile("data-storage-id=[\"'](\\d+)[\"']");

  private final StorageProviderFactory providerFactory;
  private final StorageObjectMapper storageObjectMapper;
  private final StorageObjectRefMapper storageObjectRefMapper;

  public StorageService(
      StorageProviderFactory providerFactory,
      StorageObjectMapper storageObjectMapper,
      StorageObjectRefMapper storageObjectRefMapper
  ) {
    this.providerFactory = providerFactory;
    this.storageObjectMapper = storageObjectMapper;
    this.storageObjectRefMapper = storageObjectRefMapper;
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

  @Transactional(rollbackFor = Exception.class)
  public void syncArticleImages(Long articleId, String content) {
    Set<Long> nextObjectIds = extractStorageIds(content);
    List<StorageObjectRef> currentRefs = storageObjectRefMapper.selectList(articleRefQuery(articleId));
    Set<Long> currentObjectIds = currentRefs.stream().map(StorageObjectRef::getObjectId).collect(HashSet::new, Set::add, Set::addAll);

    for (StorageObjectRef ref : currentRefs) {
      if (!nextObjectIds.contains(ref.getObjectId())) {
        storageObjectRefMapper.deleteById(ref.getId());
        deleteIfUnreferenced(ref.getObjectId());
      }
    }

    for (Long objectId : nextObjectIds) {
      if (currentObjectIds.contains(objectId)) {
        continue;
      }
      StorageObject object = storageObjectMapper.selectById(objectId);
      if (object == null) {
        continue;
      }
      StorageObjectRef ref = new StorageObjectRef();
      ref.setObjectId(objectId);
      ref.setRefType(REF_TYPE_ARTICLE);
      ref.setRefId(articleId);
      ref.setUsageType(USAGE_TYPE_CONTENT_IMAGE);
      ref.setCreatedAt(LocalDateTime.now());
      storageObjectRefMapper.insert(ref);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteArticleImages(Long articleId) {
    List<StorageObjectRef> refs = storageObjectRefMapper.selectList(articleRefQuery(articleId));
    for (StorageObjectRef ref : refs) {
      storageObjectRefMapper.deleteById(ref.getId());
      deleteIfUnreferenced(ref.getObjectId());
    }
  }

  private Set<Long> extractStorageIds(String content) {
    Set<Long> ids = new HashSet<>();
    if (content == null || content.isBlank()) {
      return ids;
    }
    Matcher matcher = STORAGE_ID_PATTERN.matcher(content);
    while (matcher.find()) {
      ids.add(Long.valueOf(matcher.group(1)));
    }
    return ids;
  }

  private LambdaQueryWrapper<StorageObjectRef> articleRefQuery(Long articleId) {
    return new LambdaQueryWrapper<StorageObjectRef>()
        .eq(StorageObjectRef::getRefType, REF_TYPE_ARTICLE)
        .eq(StorageObjectRef::getRefId, articleId)
        .eq(StorageObjectRef::getUsageType, USAGE_TYPE_CONTENT_IMAGE);
  }

  private void deleteIfUnreferenced(Long objectId) {
    Long refCount = storageObjectRefMapper.selectCount(new LambdaQueryWrapper<StorageObjectRef>().eq(StorageObjectRef::getObjectId, objectId));
    if (refCount != null && refCount > 0) {
      return;
    }
    StorageObject object = storageObjectMapper.selectById(objectId);
    if (object == null) {
      return;
    }
    providerFactory.client(object.getProviderCode()).delete(object.getBucketName(), object.getObjectKey());
    storageObjectMapper.deleteById(objectId);
  }
}
