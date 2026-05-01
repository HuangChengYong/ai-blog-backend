package com.aiblog.storage;

import com.aiblog.config.StorageProperties;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class StorageProviderFactory {

  private final StorageProperties properties;
  private final Map<String, ObjectStorageClient> clients;

  public StorageProviderFactory(StorageProperties properties, List<ObjectStorageClient> clients) {
    this.properties = properties;
    this.clients = clients.stream().collect(Collectors.toMap(ObjectStorageClient::providerCode, Function.identity()));
  }

  public ObjectStorageClient activeClient() {
    ObjectStorageClient client = clients.get(properties.activeProvider());
    if (client == null) {
      throw new IllegalStateException("No object storage client configured for provider: " + properties.activeProvider());
    }
    return client;
  }

  public ObjectStorageClient client(String providerCode) {
    ObjectStorageClient client = clients.get(providerCode);
    if (client == null) {
      throw new IllegalArgumentException("Unsupported storage provider: " + providerCode);
    }
    return client;
  }
}
