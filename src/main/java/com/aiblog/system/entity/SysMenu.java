package com.aiblog.system.entity;

import com.aiblog.common.AuditEntity;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_menu")
public class SysMenu extends AuditEntity {

  private Long parentId;
  private String title;
  private String menuType;
  private String routePath;
  private String component;
  private String permissionCode;
  private String icon;
  private Integer sortOrder;
  private Integer visible;
  private Integer keepAlive;
  private Integer external;
  private Integer status;

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMenuType() {
    return menuType;
  }

  public void setMenuType(String menuType) {
    this.menuType = menuType;
  }

  public String getRoutePath() {
    return routePath;
  }

  public void setRoutePath(String routePath) {
    this.routePath = routePath;
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public String getPermissionCode() {
    return permissionCode;
  }

  public void setPermissionCode(String permissionCode) {
    this.permissionCode = permissionCode;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public Integer getVisible() {
    return visible;
  }

  public void setVisible(Integer visible) {
    this.visible = visible;
  }

  public Integer getKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(Integer keepAlive) {
    this.keepAlive = keepAlive;
  }

  public Integer getExternal() {
    return external;
  }

  public void setExternal(Integer external) {
    this.external = external;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }
}
