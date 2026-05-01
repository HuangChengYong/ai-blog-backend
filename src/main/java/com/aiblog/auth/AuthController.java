package com.aiblog.auth;

import com.aiblog.auth.dto.LoginRequest;
import com.aiblog.auth.dto.LoginResponse;
import com.aiblog.common.ApiResponse;
import com.aiblog.common.RepeatSubmit;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @RepeatSubmit(seconds = 2)
  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.ok(authService.login(request));
  }
}
