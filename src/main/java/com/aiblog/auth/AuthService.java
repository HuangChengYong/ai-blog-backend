package com.aiblog.auth;

import com.aiblog.auth.dto.LoginRequest;
import com.aiblog.auth.dto.LoginResponse;
import com.aiblog.security.JwtTokenService;
import com.aiblog.security.SecurityUser;
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

  public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenService = jwtTokenService;
  }

  public LoginResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    SecurityUser user = (SecurityUser) authentication.getPrincipal();
    List<String> permissions = user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
    return new LoginResponse("Bearer", jwtTokenService.issue(user), String.valueOf(user.id()), user.getUsername(), user.nickname(), permissions);
  }
}
