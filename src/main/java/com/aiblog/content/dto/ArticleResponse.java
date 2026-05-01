package com.aiblog.content.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleResponse(
    String id,
    String title,
    String summary,
    String content,
    String authorId,
    String authorName,
    String categoryId,
    String category,
    List<String> tags,
    String source,
    String status,
    String listingStatus,
    String cover,
    Integer readMinutes,
    Integer heat,
    LocalDateTime publishedAt,
    LocalDateTime updatedAt
) {
}
