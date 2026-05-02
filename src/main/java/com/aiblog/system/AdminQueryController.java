package com.aiblog.system;

import com.aiblog.common.ApiResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminQueryController {

  private final AdminQueryService adminQueryService;

  public AdminQueryController(AdminQueryService adminQueryService) {
    this.adminQueryService = adminQueryService;
  }

  @PreAuthorize("hasAuthority('dashboard.view')")
  @GetMapping("/dashboard/overview")
  public ApiResponse<AdminQueryService.AdminOverviewResponse> overview() {
    return ApiResponse.ok(adminQueryService.overview());
  }

  @PreAuthorize("hasAuthority('studio.generate')")
  @GetMapping("/studio/options")
  public ApiResponse<AdminQueryService.AdminStudioOptionsResponse> studioOptions() {
    return ApiResponse.ok(adminQueryService.studioOptions());
  }

  @PreAuthorize("hasAuthority('user.view')")
  @GetMapping("/users")
  public ApiResponse<List<AdminQueryService.AdminUserResponse>> users() {
    return ApiResponse.ok(adminQueryService.users());
  }

  @PreAuthorize("hasAuthority('role.view')")
  @GetMapping("/roles")
  public ApiResponse<List<AdminQueryService.AdminRoleResponse>> roles() {
    return ApiResponse.ok(adminQueryService.roles());
  }

  @PreAuthorize("hasAuthority('permission.manage')")
  @GetMapping("/permissions")
  public ApiResponse<List<AdminQueryService.AdminPermissionGroupResponse>> permissions() {
    return ApiResponse.ok(adminQueryService.permissions());
  }

  @PreAuthorize("hasAuthority('menu.manage')")
  @GetMapping("/menus")
  public ApiResponse<List<AdminQueryService.AdminMenuResponse>> menus() {
    return ApiResponse.ok(adminQueryService.menus());
  }
}
