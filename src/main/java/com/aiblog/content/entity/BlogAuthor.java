package com.aiblog.content.entity;

import com.aiblog.common.AuditEntity;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("blog_author")
public class BlogAuthor extends AuditEntity {

  private String name;
  private String role;
  private String avatarUrl;
  private String bio;
  private String aiPreference;
  private Integer status;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getAiPreference() {
    return aiPreference;
  }

  public void setAiPreference(String aiPreference) {
    this.aiPreference = aiPreference;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }
}
