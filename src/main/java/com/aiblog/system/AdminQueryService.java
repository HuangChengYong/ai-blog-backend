package com.aiblog.system;

import com.aiblog.content.ArticleStatus;
import com.aiblog.content.entity.BlogArticle;
import com.aiblog.content.entity.BlogTag;
import com.aiblog.content.mapper.BlogArticleMapper;
import com.aiblog.content.mapper.BlogTagMapper;
import com.aiblog.system.entity.SysMenu;
import com.aiblog.system.entity.SysPermission;
import com.aiblog.system.entity.SysRole;
import com.aiblog.system.entity.SysUser;
import com.aiblog.system.mapper.SysMenuMapper;
import com.aiblog.system.mapper.SysPermissionMapper;
import com.aiblog.system.mapper.SysRoleMapper;
import com.aiblog.system.mapper.SysRolePermissionMapper;
import com.aiblog.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminQueryService {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final BlogArticleMapper articleMapper;
  private final BlogTagMapper tagMapper;
  private final SysUserMapper userMapper;
  private final SysRoleMapper roleMapper;
  private final SysRolePermissionMapper rolePermissionMapper;
  private final SysPermissionMapper permissionMapper;
  private final SysMenuMapper menuMapper;

  public AdminQueryService(
      BlogArticleMapper articleMapper,
      BlogTagMapper tagMapper,
      SysUserMapper userMapper,
      SysRoleMapper roleMapper,
      SysRolePermissionMapper rolePermissionMapper,
      SysPermissionMapper permissionMapper,
      SysMenuMapper menuMapper
  ) {
    this.articleMapper = articleMapper;
    this.tagMapper = tagMapper;
    this.userMapper = userMapper;
    this.roleMapper = roleMapper;
    this.rolePermissionMapper = rolePermissionMapper;
    this.permissionMapper = permissionMapper;
    this.menuMapper = menuMapper;
  }

  public AdminOverviewResponse overview() {
    long total = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>());
    long review = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getStatus, ArticleStatus.REVIEW));
    long listed = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getListingStatus, ArticleStatus.LISTED));
    long ready = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>().in(BlogArticle::getStatus, ArticleStatus.READY, ArticleStatus.PUBLISHED));
    long enabledMenus = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getVisible, 1).eq(SysMenu::getStatus, 1));
    return new AdminOverviewResponse(
        List.of(
            new AdminMetricResponse("全部内容", total),
            new AdminMetricResponse("待审核", review),
            new AdminMetricResponse("已上架", listed),
            new AdminMetricResponse("可发布", ready),
            new AdminMetricResponse("启用菜单", enabledMenus)
        ),
        92,
        "内容池状态良好，建议优先处理待审核文章。"
    );
  }

  public AdminStudioOptionsResponse studioOptions() {
    List<String> tags = tagMapper.selectList(new LambdaQueryWrapper<BlogTag>().orderByDesc(BlogTag::getArticleCount).orderByAsc(BlogTag::getId))
        .stream()
        .map(BlogTag::getName)
        .toList();
    return new AdminStudioOptionsResponse(
        List.of("工程实践", "产品分析", "教程指南"),
        List.of("短篇", "中篇", "长篇"),
        tags.isEmpty() ? List.of("Agent", "RAG", "AI 写作", "Vue", "评测") : tags
    );
  }

  public List<AdminUserResponse> users() {
    return userMapper.selectList(new LambdaQueryWrapper<SysUser>().orderByAsc(SysUser::getId))
        .stream()
        .map(user -> new AdminUserResponse(
            idOf(user.getId()),
            user.getUsername(),
            roleName(user.getId()),
            Integer.valueOf(1).equals(user.getStatus()) ? "启用" : "停用",
            dataScope(user.getDataScope()),
            formatLastSeen(user.getLastLoginAt())
        ))
        .toList();
  }

  public List<AdminRoleResponse> roles() {
    return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSortOrder).orderByAsc(SysRole::getId))
        .stream()
        .map(role -> new AdminRoleResponse(
            idOf(role.getId()),
            role.getRoleName(),
            roleMapper.countUsersByRoleId(role.getId()),
            role.getDescription(),
            roleMapper.selectPermissionNamesByRoleId(role.getId()).stream().limit(6).toList(),
            roleMapper.selectPermissionCodesByRoleId(role.getId()).stream().limit(6).toList()
        ))
        .toList();
  }

  public AdminRoleResponse role(Long id) {
    SysRole role = roleMapper.selectById(id);
    if (role == null || Integer.valueOf(1).equals(role.getDeleted())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
    }

    return new AdminRoleResponse(
        idOf(role.getId()),
        role.getRoleName(),
        roleMapper.countUsersByRoleId(role.getId()),
        role.getDescription(),
        roleMapper.selectPermissionNamesByRoleId(role.getId()),
        roleMapper.selectPermissionCodesByRoleId(role.getId())
    );
  }

  @Transactional
  public AdminRoleResponse updateRole(Long id, UpdateRoleRequest request) {
    SysRole role = roleMapper.selectById(id);
    if (role == null || Integer.valueOf(1).equals(role.getDeleted())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
    }
    if (request.name() == null || request.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name is required");
    }

    role.setRoleName(request.name().trim());
    role.setDescription(request.description() == null ? "" : request.description().trim());
    role.setUpdatedAt(LocalDateTime.now());
    roleMapper.updateById(role);

    Set<String> permissionCodes = new LinkedHashSet<>(request.permissionCodes() == null ? List.of() : request.permissionCodes());
    List<SysPermission> permissions = permissionCodes.isEmpty()
        ? List.of()
        : permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
            .in(SysPermission::getCode, permissionCodes)
            .eq(SysPermission::getStatus, 1)
        );
    if (permissions.size() != permissionCodes.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid permission codes");
    }

    rolePermissionMapper.delete(new LambdaQueryWrapper<com.aiblog.system.entity.SysRolePermission>()
        .eq(com.aiblog.system.entity.SysRolePermission::getRoleId, role.getId()));
    for (SysPermission permission : permissions) {
      com.aiblog.system.entity.SysRolePermission rolePermission = new com.aiblog.system.entity.SysRolePermission();
      rolePermission.setRoleId(role.getId());
      rolePermission.setPermissionId(permission.getId());
      rolePermission.setCreatedAt(LocalDateTime.now());
      rolePermissionMapper.insert(rolePermission);
    }

    return role(role.getId());
  }

  public List<AdminPermissionGroupResponse> permissions() {
    List<SysPermission> permissions = permissionMapper.selectList(
        new LambdaQueryWrapper<SysPermission>()
            .eq(SysPermission::getStatus, 1)
            .orderByAsc(SysPermission::getResource)
            .orderByAsc(SysPermission::getId)
    );
    Map<String, List<AdminPermissionItemResponse>> groups = new LinkedHashMap<>();
    for (SysPermission permission : permissions) {
      groups.computeIfAbsent(permissionGroup(permission.getResource()), key -> new ArrayList<>())
          .add(new AdminPermissionItemResponse(permission.getCode(), permission.getName(), permission.getDescription()));
    }
    return groups.entrySet().stream()
        .map(entry -> new AdminPermissionGroupResponse(entry.getKey(), entry.getValue()))
        .toList();
  }

  public List<AdminMenuResponse> menus() {
    return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder).orderByAsc(SysMenu::getId))
        .stream()
        .map(menu -> new AdminMenuResponse(
            idOf(menu.getId()),
            menu.getTitle(),
            menu.getRoutePath(),
            menu.getPermissionCode(),
            menu.getSortOrder() == null ? 0 : menu.getSortOrder(),
            Integer.valueOf(1).equals(menu.getVisible()),
            Integer.valueOf(1).equals(menu.getStatus()) ? "enabled" : "disabled",
            true
        ))
        .toList();
  }

  private String roleName(Long userId) {
    String roleName = userMapper.selectPrimaryRoleNameByUserId(userId);
    return roleName == null || roleName.isBlank() ? "未分配角色" : roleName;
  }

  private String dataScope(String scope) {
    if ("ALL".equalsIgnoreCase(scope)) {
      return "全部权限";
    }
    if ("SELF".equalsIgnoreCase(scope)) {
      return "本人数据";
    }
    return scope == null || scope.isBlank() ? "未配置" : scope;
  }

  private String formatLastSeen(LocalDateTime value) {
    return value == null ? "暂无登录记录" : DATE_TIME_FORMATTER.format(value);
  }

  private String permissionGroup(String resource) {
    if (resource == null) {
      return "其他";
    }
    return switch (resource) {
      case "dashboard", "studio", "article", "approval", "comment", "media" -> "内容运营";
      case "user", "role", "permission", "menu" -> "用户角色";
      case "config", "audit", "data", "notice" -> "系统安全";
      default -> "其他";
    };
  }

  private String idOf(Long id) {
    return id == null ? null : String.valueOf(id);
  }

  public record AdminMetricResponse(String label, long value) {
  }

  public record AdminOverviewResponse(List<AdminMetricResponse> stats, int publishHealth, String healthText) {
  }

  public record AdminStudioOptionsResponse(List<String> styles, List<String> lengths, List<String> tags) {
  }

  public record AdminUserResponse(String id, String name, String role, String status, String scope, String lastSeen) {
  }

  public record AdminRoleResponse(
      String id,
      String name,
      long users,
      String description,
      List<String> permissions,
      List<String> permissionCodes
  ) {
  }

  public record UpdateRoleRequest(String name, String description, List<String> permissionCodes) {
  }

  public record AdminPermissionItemResponse(String code, String name, String description) {
  }

  public record AdminPermissionGroupResponse(String title, List<AdminPermissionItemResponse> items) {
  }

  public record AdminMenuResponse(
      String id,
      String title,
      String path,
      String permission,
      int order,
      boolean visible,
      String status,
      boolean system
  ) {
  }
}
