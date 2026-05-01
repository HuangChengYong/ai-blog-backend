package com.aiblog.system.dto;

import com.aiblog.system.entity.SysMenu;
import java.util.List;

public record CurrentUserResponse(
    String id,
    String username,
    String nickname,
    List<String> permissions,
    List<SysMenu> menus
) {
}
