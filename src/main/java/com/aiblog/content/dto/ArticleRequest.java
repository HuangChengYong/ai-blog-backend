package com.aiblog.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ArticleRequest(
    @NotBlank(message = "标题不能为空") @Size(max = 180, message = "标题不能超过180个字符") String title,
    @NotBlank(message = "摘要不能为空") @Size(max = 512, message = "摘要不能超过512个字符") String summary,
    @NotBlank(message = "正文不能为空") String content,
    String authorId,
    String categoryId,
    @NotEmpty(message = "至少选择一个标签") List<String> tags,
    String source
) {
}
