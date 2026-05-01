package com.aiblog.content.dto;

import java.util.List;

public record PublicTopicResponse(String id, String title, String description, List<String> tags) {
}
