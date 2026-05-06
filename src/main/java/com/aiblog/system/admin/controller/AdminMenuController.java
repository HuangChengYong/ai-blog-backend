package com.aiblog.system.admin.controller;

import com.aiblog.common.ApiResponse;
import com.aiblog.system.admin.service.AdminMenuService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/menus")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }

    @PreAuthorize("hasAuthority('menu.manage')")
    @GetMapping
    public ApiResponse<List<AdminMenuService.AdminMenuResponse>> menus() {
        return ApiResponse.ok(adminMenuService.menus());
    }

    @PreAuthorize("hasAuthority('menu.manage')")
    @PostMapping
    public ApiResponse<AdminMenuService.AdminMenuResponse> createMenu(
            @Valid @RequestBody AdminMenuService.CreateMenuRequest request) {
        return ApiResponse.ok(adminMenuService.createMenu(request));
    }

    @PreAuthorize("hasAuthority('menu.manage')")
    @PutMapping("/{id}")
    public ApiResponse<AdminMenuService.AdminMenuResponse> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody AdminMenuService.UpdateMenuRequest request) {
        return ApiResponse.ok(adminMenuService.updateMenu(id, request));
    }

    @PreAuthorize("hasAuthority('menu.manage')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMenu(@PathVariable Long id) {
        adminMenuService.deleteMenu(id);
        return ApiResponse.ok();
    }
}