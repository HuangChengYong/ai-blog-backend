package com.aiblog.system.admin.service.impl;

import com.aiblog.system.admin.service.AdminPermissionService;
import com.aiblog.system.entity.SysPermission;
import com.aiblog.system.mapper.SysPermissionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final SysPermissionMapper permissionMapper;

    public AdminPermissionServiceImpl(SysPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<AdminPermissionGroupResponse> permissions() {
        List<SysPermission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getStatus, 1).orderByAsc(SysPermission::getResource).orderByAsc(SysPermission::getId)
        );
        Map<String, List<AdminPermissionItemResponse>> groups = new LinkedHashMap<>();
        for (SysPermission permission : permissions) {
            groups.computeIfAbsent(permissionGroup(permission.getResource()), key -> new ArrayList<>())
                    .add(new AdminPermissionItemResponse(permission.getCode(), permission.getName(), permission.getDescription()));
        }
        return groups.entrySet().stream().map(entry -> new AdminPermissionGroupResponse(entry.getKey(), entry.getValue())).toList();
    }

    private String permissionGroup(String resource) {
        if (resource == null) return "其他";
        return switch (resource) {
            case "dashboard", "studio", "article", "approval", "comment", "media" -> "内容运营";
            case "user", "role", "permission", "menu" -> "用户角色";
            case "config", "audit", "data", "notice" -> "系统安全";
            default -> "其他";
        };
    }
}