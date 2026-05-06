package com.aiblog.system.admin.controller;

import com.aiblog.common.ApiResponse;
import com.aiblog.system.admin.service.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @PreAuthorize("hasAuthority('dashboard.view')")
    @GetMapping("/overview")
    public ApiResponse<AdminDashboardService.AdminOverviewResponse> overview() {
        return ApiResponse.ok(adminDashboardService.overview());
    }
}