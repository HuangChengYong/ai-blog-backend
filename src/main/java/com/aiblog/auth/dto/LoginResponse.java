package com.aiblog.auth.dto;

import java.util.List;

public record LoginResponse(
    String tokenType,
    String accessToken,
    String userId,
    String username,
    String nickname,
    String avatarUrl,
    String roleName,
    String dataScope,
    List<String> permissions
) {
}
