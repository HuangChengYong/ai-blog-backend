package com.aiblog.system.admin.service;

import java.util.List;

public interface AdminRoleService {

    List<AdminRoleResponse> roles();

    AdminRoleResponse role(Long id);

    AdminRoleResponse createRole(CreateRoleRequest request);

    AdminRoleResponse updateRole(Long id, UpdateRoleRequest request);

    void deleteRole(Long id);

    record AdminRoleResponse(String id, String name, long users, String description, List<String> permissions, List<String> permissionCodes) {}
    record CreateRoleRequest(String name, String description, String roleCode, List<String> permissionCodes) {}
    record UpdateRoleRequest(String name, String description, List<String> permissionCodes) {}
}