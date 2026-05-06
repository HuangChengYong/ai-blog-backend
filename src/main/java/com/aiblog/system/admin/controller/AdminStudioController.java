package com.aiblog.system.admin.controller;

import com.aiblog.common.ApiResponse;
import com.aiblog.system.admin.service.AdminStudioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/studio")
public class AdminStudioController {

    private final AdminStudioService adminStudioService;

    public AdminStudioController(AdminStudioService adminStudioService) {
        this.adminStudioService = adminStudioService;
    }

    @PreAuthorize("hasAuthority('studio.generate')")
    @GetMapping("/options")
    public ApiResponse<AdminStudioService.AdminStudioOptionsResponse> studioOptions() {
        return ApiResponse.ok(adminStudioService.studioOptions());
    }
}