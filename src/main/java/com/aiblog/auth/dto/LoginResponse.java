package com.aiblog.auth.dto;

import java.util.List;

public record LoginResponse(
    String tokenType,
    String accessToken,
    String userId,
    String username,
    String nickname,
    List<String> permissions
) {
}
