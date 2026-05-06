package com.aiblog.system.admin.controller;

import com.aiblog.common.ApiResponse;
import com.aiblog.system.admin.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PreAuthorize("hasAuthority('user.view')")
    @GetMapping
    public ApiResponse<List<AdminUserService.AdminUserResponse>> users() {
        return ApiResponse.ok(adminUserService.users());
    }

    @PreAuthorize("hasAuthority('user.view')")
    @GetMapping("/{id}")
    public ApiResponse<AdminUserService.AdminUserResponse> user(@PathVariable Long id) {
        return ApiResponse.ok(adminUserService.user(id));
    }

    @PreAuthorize("hasAuthority('user.create')")
    @PostMapping
    public ApiResponse<AdminUserService.AdminUserResponse> createUser(
            @Valid @RequestBody AdminUserService.CreateUserRequest request) {
        return ApiResponse.ok(adminUserService.createUser(request));
    }

    @PreAuthorize("hasAuthority('user.update')")
    @PutMapping("/{id}")
    public ApiResponse<AdminUserService.AdminUserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserService.UpdateUserRequest request) {
        return ApiResponse.ok(adminUserService.updateUser(id, request));
    }

    @PreAuthorize("hasAuthority('user.disable')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ApiResponse.ok();
    }

    @PreAuthorize("hasAuthority('user.disable')")
    @PutMapping("/{id}/status")
    public ApiResponse<AdminUserService.AdminUserResponse> toggleStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return ApiResponse.ok(adminUserService.toggleStatus(id, status));
    }
}