package com.aiblog.content.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record GenerateArticleRequest(
    @NotBlank(message = "请输入文章主题") String prompt,
    String style,
    String length,
    List<String> tags
) {
}
