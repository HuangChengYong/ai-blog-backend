package com.aiblog.system.admin.service;

import java.util.List;

public interface AdminMenuService {

    List<AdminMenuResponse> menus();

    AdminMenuResponse createMenu(CreateMenuRequest request);

    AdminMenuResponse updateMenu(Long id, UpdateMenuRequest request);

    void deleteMenu(Long id);

    record AdminMenuResponse(String id, String title, String path, String permission, int order, boolean visible, String status, boolean system) {}
    record CreateMenuRequest(String title, String path, String permission, Integer order, Integer visible, Integer status) {}
    record UpdateMenuRequest(String title, String path, String permission, Integer order, Integer visible, Integer status) {}
}