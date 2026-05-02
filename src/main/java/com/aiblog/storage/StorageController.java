package com.aiblog.storage;

import com.aiblog.common.ApiResponse;
import com.aiblog.common.BusinessException;
import com.aiblog.common.ErrorCode;
import com.aiblog.common.RepeatSubmit;
import com.aiblog.storage.entity.StorageObject;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/storage")
public class StorageController {

  private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

  private final StorageService storageService;

  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PreAuthorize("hasAuthority('media.manage')")
  @RepeatSubmit(seconds = 10, message = "文件正在上传，请勿重复提交")
  @PostMapping("/objects")
  public ApiResponse<StorageObject> upload(@RequestPart("file") MultipartFile file) {
    validateImageFile(file);
    return ApiResponse.ok(storageService.upload(file));
  }

  private void validateImageFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ErrorCode.FILE_EMPTY, ErrorCode.FILE_EMPTY.message());
    }
    if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
      throw new BusinessException(ErrorCode.FILE_TYPE_INVALID, ErrorCode.FILE_TYPE_INVALID.message());
    }
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED, ErrorCode.FILE_SIZE_EXCEEDED.message());
    }
  }
}
