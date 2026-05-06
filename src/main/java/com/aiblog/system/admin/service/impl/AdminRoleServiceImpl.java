package com.aiblog.system.admin.service.impl;

import com.aiblog.system.admin.service.AdminRoleService;
import com.aiblog.system.entity.SysPermission;
import com.aiblog.system.entity.SysRole;
import com.aiblog.system.entity.SysRolePermission;
import com.aiblog.system.mapper.SysPermissionMapper;
import com.aiblog.system.mapper.SysRoleMapper;
import com.aiblog.system.mapper.SysRolePermissionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;

    public AdminRoleServiceImpl(SysRoleMapper roleMapper, SysRolePermissionMapper rolePermissionMapper, SysPermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<AdminRoleResponse> roles() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSortOrder).orderByAsc(SysRole::getId))
                .stream()
                .map(role -> new AdminRoleResponse(
                        String.valueOf(role.getId()), role.getRoleName(), roleMapper.countUsersByRoleId(role.getId()), role.getDescription(),
                        roleMapper.selectPermissionNamesByRoleId(role.getId()).stream().limit(6).toList(),
                        roleMapper.selectPermissionCodesByRoleId(role.getId()).stream().limit(6).toList()
                )).toList();
    }

    @Override
    public AdminRoleResponse role(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || Integer.valueOf(1).equals(role.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        return new AdminRoleResponse(
                String.valueOf(role.getId()), role.getRoleName(), roleMapper.countUsersByRoleId(role.getId()), role.getDescription(),
                roleMapper.selectPermissionNamesByRoleId(role.getId()),
                roleMapper.selectPermissionCodesByRoleId(role.getId())
        );
    }

    @Transactional
    @Override
    public AdminRoleResponse createRole(CreateRoleRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name is required");
        }
        if (request.roleCode() == null || request.roleCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role code is required");
        }
        SysRole existing = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, request.roleCode()));
        if (existing != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role code already exists");
        }

        SysRole role = new SysRole();
        role.setRoleCode(request.roleCode().trim().toUpperCase());
        role.setRoleName(request.name().trim());
        role.setDescription(request.description() == null ? "" : request.description().trim());
        role.setDataScope("SELF");
        role.setSortOrder(0);
        role.setStatus(1);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(role);

        Set<String> permissionCodes = new LinkedHashSet<>(request.permissionCodes() == null ? List.of() : request.permissionCodes());
        List<SysPermission> permissions = permissionCodes.isEmpty() ? List.of() : permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                                                                                                              .in(SysPermission::getCode, permissionCodes).eq(SysPermission::getStatus, 1));
        if (permissions.size() != permissionCodes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid permission codes");
        }

        for (SysPermission permission : permissions) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permission.getId());
            rolePermission.setCreatedAt(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }

        return role(role.getId());
    }

    @Transactional
    @Override
    public AdminRoleResponse updateRole(Long id, UpdateRoleRequest request) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || Integer.valueOf(1).equals(role.getDeleted())) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        if (request.name() == null || request.name().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name is required");

        role.setRoleName(request.name().trim());
        role.setDescription(request.description() == null ? "" : request.description().trim());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(role);

        Set<String> permissionCodes = new LinkedHashSet<>(request.permissionCodes() == null ? List.of() : request.permissionCodes());
        List<SysPermission> permissions = permissionCodes.isEmpty() ? List.of() : permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                                                                                                              .in(SysPermission::getCode, permissionCodes).eq(SysPermission::getStatus, 1));

        if (permissions.size() != permissionCodes.size()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid permission codes");

        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId()));
        for (SysPermission permission : permissions) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permission.getId());
            rolePermission.setCreatedAt(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }
        return role(role.getId());
    }

    @Transactional
    @Override
    public void deleteRole(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || Integer.valueOf(1).equals(role.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        long userCount = roleMapper.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role has associated users");
        }
        role.setDeleted(1);
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(role);
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
    }
}