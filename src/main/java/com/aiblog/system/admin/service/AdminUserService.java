package com.aiblog.system.admin.service;

import java.util.List;

public interface AdminUserService {

    List<AdminUserResponse> users();

    AdminUserResponse user(Long id);

    AdminUserResponse createUser(CreateUserRequest request);

    AdminUserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    AdminUserResponse toggleStatus(Long id, Integer status);

    record AdminUserResponse(
            String id,
            String name,
            String nickname,
            String avatarUrl,
            String roleId,
            String role,
            String status,
            Integer statusValue,
            String scope,
            String dataScope,
            String lastSeen
    ) {}
    record CreateUserRequest(String username, String password, String nickname, String avatarUrl, String roleId, String dataScope, Integer status) {}
    record UpdateUserRequest(String username, String password, String nickname, String avatarUrl, String roleId, String dataScope, Integer status) {}
}
