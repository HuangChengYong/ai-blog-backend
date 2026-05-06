package com.aiblog.system.admin.service;

import java.util.List;

public interface AdminPermissionService {

    List<AdminPermissionGroupResponse> permissions();

    record AdminPermissionItemResponse(String code, String name, String description) {}
    record AdminPermissionGroupResponse(String title, List<AdminPermissionItemResponse> items) {}
}