package com.aiblog.system.admin.service.impl;

import com.aiblog.system.admin.service.AdminMenuService;
import com.aiblog.system.entity.SysMenu;
import com.aiblog.system.mapper.SysMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    private final SysMenuMapper menuMapper;

    public AdminMenuServiceImpl(SysMenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<AdminMenuResponse> menus() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder).orderByAsc(SysMenu::getId))
                .stream()
                .map(menu -> toResponse(menu))
                .toList();
    }

    @Override
    public AdminMenuResponse createMenu(CreateMenuRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu title is required");
        }
        if (request.path() == null || request.path().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu path is required");
        }
        SysMenu menu = new SysMenu();
        menu.setParentId(0L);
        menu.setTitle(request.title().trim());
        menu.setRoutePath(request.path().trim());
        menu.setPermissionCode(request.permission());
        menu.setMenuType("MENU");
        menu.setSortOrder(request.order() == null ? 0 : request.order());
        menu.setVisible(request.visible() == null ? 1 : request.visible());
        menu.setStatus(request.status() == null ? 1 : request.status());
        menu.setKeepAlive(0);
        menu.setExternal(0);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        menuMapper.insert(menu);
        return toResponse(menu);
    }

    @Override
    public AdminMenuResponse updateMenu(Long id, UpdateMenuRequest request) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null || Integer.valueOf(1).equals(menu.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found");
        }
        if (request.title() != null && !request.title().isBlank()) {
            menu.setTitle(request.title().trim());
        }
        if (request.path() != null && !request.path().isBlank()) {
            menu.setRoutePath(request.path().trim());
        }
        if (request.permission() != null) {
            menu.setPermissionCode(request.permission().isBlank() ? null : request.permission());
        }
        if (request.order() != null) {
            menu.setSortOrder(request.order());
        }
        if (request.visible() != null) {
            menu.setVisible(request.visible());
        }
        if (request.status() != null) {
            menu.setStatus(request.status());
        }
        menu.setUpdatedAt(LocalDateTime.now());
        menuMapper.updateById(menu);
        return toResponse(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null || Integer.valueOf(1).equals(menu.getDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found");
        }
        menu.setDeleted(1);
        menu.setUpdatedAt(LocalDateTime.now());
        menuMapper.updateById(menu);
    }

    private AdminMenuResponse toResponse(SysMenu menu) {
        return new AdminMenuResponse(
                String.valueOf(menu.getId()),
                menu.getTitle(),
                menu.getRoutePath(),
                menu.getPermissionCode(),
                menu.getSortOrder() == null ? 0 : menu.getSortOrder(),
                Integer.valueOf(1).equals(menu.getVisible()),
                Integer.valueOf(1).equals(menu.getStatus()) ? "enabled" : "disabled",
                true
        );
    }
}