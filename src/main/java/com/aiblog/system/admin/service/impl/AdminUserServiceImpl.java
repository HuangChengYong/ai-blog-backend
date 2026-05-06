package com.aiblog.system.admin.service.impl;

import com.aiblog.system.admin.service.AdminUserService;
import com.aiblog.system.entity.SysRole;
import com.aiblog.system.entity.SysUserRole;
import com.aiblog.system.entity.SysUser;
import com.aiblog.system.mapper.SysRoleMapper;
import com.aiblog.system.mapper.SysUserMapper;
import com.aiblog.system.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(SysUserMapper userMapper, SysUserRoleMapper userRoleMapper, SysRoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AdminUserResponse> users() {
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>().orderByAsc(SysUser::getId))
                .stream()
                .map(user -> toResponse(user))
                .toList();
    }

    @Override
    public AdminUserResponse user(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return toResponse(user);
    }

    @Transactional
    @Override
    public AdminUserResponse createUser(CreateUserRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        SysUser existing = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.username()));
        if (existing != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        SysUser user = new SysUser();
        user.setUsername(request.username().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname() == null || request.nickname().isBlank() ? request.username().trim() : request.nickname().trim());
        user.setAvatarUrl(blankToNull(request.avatarUrl()));
        user.setDataScope(request.dataScope() == null || request.dataScope().isBlank() ? "SELF" : request.dataScope());
        user.setStatus(request.status() == null ? 1 : request.status());
        user.setUserType("NORMAL");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        Long roleId = resolveCreateRoleId(request.roleId());
        if (roleId != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRole.setCreatedAt(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }

        return toResponse(user);
    }

    @Transactional
    @Override
    public AdminUserResponse updateUser(Long id, UpdateUserRequest request) {
        SysUser user = userMapper.selectById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (request.username() != null && !request.username().isBlank()) {
            SysUser existing = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, request.username())
                    .ne(SysUser::getId, id));
            if (existing != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
            }
            user.setUsername(request.username().trim());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
            user.setPasswordUpdatedAt(LocalDateTime.now());
        }
        if (request.nickname() != null && !request.nickname().isBlank()) {
            user.setNickname(request.nickname().trim());
        }
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(blankToNull(request.avatarUrl()));
        }
        if (request.dataScope() != null && !request.dataScope().isBlank()) {
            user.setDataScope(request.dataScope());
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        if (request.roleId() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
            if (!request.roleId().isBlank()) {
                Long roleId = parseRoleId(request.roleId());
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRole.setCreatedAt(LocalDateTime.now());
                userRoleMapper.insert(userRole);
            }
        }

        return toResponse(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setDeleted(1);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Transactional
    @Override
    public AdminUserResponse toggleStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return toResponse(user);
    }

    private AdminUserResponse toResponse(SysUser user) {
        Long roleId = userMapper.selectPrimaryRoleIdByUserId(user.getId());
        return new AdminUserResponse(
                user.getId() == null ? null : String.valueOf(user.getId()),
                user.getUsername(),
                user.getNickname(),
                user.getAvatarUrl(),
                roleId == null ? "" : String.valueOf(roleId),
                roleName(user.getId()),
                Integer.valueOf(1).equals(user.getStatus()) ? "启用" : "停用",
                user.getStatus(),
                dataScope(user.getDataScope()),
                user.getDataScope(),
                formatLastSeen(user.getLastLoginAt())
        );
    }

    private Long resolveCreateRoleId(String requestedRoleId) {
        if (requestedRoleId != null && !requestedRoleId.isBlank()) {
            return parseRoleId(requestedRoleId);
        }
        SysRole defaultRole = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "CONTENT_EDITOR")
                .eq(SysRole::getStatus, 1)
                .eq(SysRole::getDeleted, 0));
        return defaultRole == null ? null : defaultRole.getId();
    }

    private Long parseRoleId(String rawRoleId) {
        Long roleId;
        try {
            roleId = Long.valueOf(rawRoleId);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role id");
        }
        SysRole role = roleMapper.selectById(roleId);
        if (role == null || Integer.valueOf(1).equals(role.getDeleted()) || !Integer.valueOf(1).equals(role.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role id");
        }
        return roleId;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String roleName(Long userId) {
        String roleName = userMapper.selectPrimaryRoleNameByUserId(userId);
        return roleName == null || roleName.isBlank() ? "未分配角色" : roleName;
    }

    private String dataScope(String scope) {
        if ("ALL".equalsIgnoreCase(scope)) return "全部权限";
        if ("SELF".equalsIgnoreCase(scope)) return "本人数据";
        return scope == null || scope.isBlank() ? "未配置" : scope;
    }

    private String formatLastSeen(LocalDateTime value) {
        return value == null ? "暂无登录记录" : DATE_TIME_FORMATTER.format(value);
    }
}
