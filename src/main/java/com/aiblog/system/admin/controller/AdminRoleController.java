package com.aiblog.system.admin.controller;

import com.aiblog.common.ApiResponse;
import com.aiblog.system.admin.service.AdminRoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/roles")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    public AdminRoleController(AdminRoleService adminRoleService) {
        this.adminRoleService = adminRoleService;
    }

    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping
    public ApiResponse<List<AdminRoleService.AdminRoleResponse>> roles() {
        return ApiResponse.ok(adminRoleService.roles());
    }

    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping("/{id}")
    public ApiResponse<AdminRoleService.AdminRoleResponse> role(@PathVariable Long id) {
        return ApiResponse.ok(adminRoleService.role(id));
    }

    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping
    public ApiResponse<AdminRoleService.AdminRoleResponse> createRole(
            @Valid @RequestBody AdminRoleService.CreateRoleRequest request) {
        return ApiResponse.ok(adminRoleService.createRole(request));
    }

    @PreAuthorize("hasAuthority('role.update')")
    @PutMapping("/{id}")
    public ApiResponse<AdminRoleService.AdminRoleResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminRoleService.UpdateRoleRequest request) {
        return ApiResponse.ok(adminRoleService.updateRole(id, request));
    }

    @PreAuthorize("hasAuthority('role.update')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        return ApiResponse.ok();
    }
}