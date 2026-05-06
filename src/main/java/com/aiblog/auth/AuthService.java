package com.aiblog.auth;

import com.aiblog.auth.dto.LoginRequest;
import com.aiblog.auth.dto.LoginResponse;
import com.aiblog.security.JwtTokenService;
import com.aiblog.security.SecurityUser;
import com.aiblog.system.mapper.SysUserMapper;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenService jwtTokenService;
  private final SysUserMapper userMapper;

  public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, SysUserMapper userMapper) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenService = jwtTokenService;
    this.userMapper = userMapper;
  }

  public LoginResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    SecurityUser user = (SecurityUser) authentication.getPrincipal();
    List<String> permissions = user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
    String roleName = userMapper.selectPrimaryRoleNameByUserId(user.id());
    if (roleName == null || roleName.isBlank()) {
      roleName = "未分配角色";
    }
    return new LoginResponse(
        "Bearer",
        jwtTokenService.issue(user),
        String.valueOf(user.id()),
        user.getUsername(),
        user.nickname(),
        user.avatarUrl(),
        roleName,
        user.dataScope(),
        permissions
    );
  }
}
