package com.aiblog.system.admin.service;

import java.util.List;

public interface AdminDashboardService {

    AdminOverviewResponse overview();

    // 接口内部声明 DTO，Controller 可直接使用 AdminDashboardService.AdminOverviewResponse 引用
    record AdminMetricResponse(String label, long value) {}
    record AdminOverviewResponse(List<AdminMetricResponse> stats, int publishHealth, String healthText) {}
}