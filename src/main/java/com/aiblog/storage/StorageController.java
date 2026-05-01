package com.aiblog.storage;

import com.aiblog.common.ApiResponse;
import com.aiblog.common.RepeatSubmit;
import com.aiblog.storage.entity.StorageObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/storage")
public class StorageController {

  private final StorageService storageService;

  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PreAuthorize("hasAuthority('media.manage')")
  @RepeatSubmit(seconds = 10, message = "文件正在上传，请勿重复提交")
  @PostMapping("/objects")
  public ApiResponse<StorageObject> upload(@RequestPart("file") MultipartFile file) {
    return ApiResponse.ok(storageService.upload(file));
  }
}
