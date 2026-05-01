package com.aiblog.system;

import com.aiblog.common.ApiResponse;
import com.aiblog.common.BusinessException;
import com.aiblog.common.ErrorCode;
import com.aiblog.common.RepeatSubmit;
import com.aiblog.security.SecurityUser;
import com.aiblog.system.dto.ChangePasswordRequest;
import com.aiblog.system.dto.CurrentUserResponse;
import com.aiblog.system.entity.SysUser;
import com.aiblog.system.mapper.SysMenuMapper;
import com.aiblog.system.mapper.SysPermissionMapper;
import com.aiblog.system.mapper.SysUserMapper;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/current")
public class AdminCurrentController {

  private final SysPermissionMapper permissionMapper;
  private final SysMenuMapper menuMapper;
  private final SysUserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public AdminCurrentController(
      SysPermissionMapper permissionMapper,
      SysMenuMapper menuMapper,
      SysUserMapper userMapper,
      PasswordEncoder passwordEncoder
  ) {
    this.permissionMapper = permissionMapper;
    this.menuMapper = menuMapper;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public ApiResponse<CurrentUserResponse> current(@AuthenticationPrincipal SecurityUser user) {
    CurrentUserResponse response = new CurrentUserResponse(
        String.valueOf(user.id()),
        user.getUsername(),
        user.nickname(),
        permissionMapper.selectPermissionCodesByUserId(user.id()),
        menuMapper.selectVisibleMenusByUserId(user.id())
    );
    return ApiResponse.ok(response);
  }

  @RepeatSubmit
  @PutMapping("/password")
  public ApiResponse<Void> changePassword(
      @AuthenticationPrincipal SecurityUser user,
      @Valid @RequestBody ChangePasswordRequest request
  ) {
    SysUser sysUser = userMapper.selectById(user.id());
    if (sysUser == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
    }
    if (!passwordEncoder.matches(request.currentPassword(), sysUser.getPasswordHash())) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "当前密码不正确");
    }
    sysUser.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    sysUser.setPasswordUpdatedAt(LocalDateTime.now());
    sysUser.setUpdatedBy(user.id());
    sysUser.setUpdatedAt(LocalDateTime.now());
    userMapper.updateById(sysUser);
    return ApiResponse.ok();
  }
}
