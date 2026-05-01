package com.aiblog.content.entity;

import com.aiblog.common.AuditEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("blog_article")
public class BlogArticle extends AuditEntity {

  private String title;
  private String summary;
  private Long authorId;
  private Long categoryId;
  private String source;
  private String status;
  private String listingStatus;
  private String coverType;
  private String coverValue;
  private Integer readMinutes;
  private Integer heat;
  private LocalDateTime submittedAt;
  private LocalDateTime approvedAt;
  private LocalDateTime publishedAt;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getListingStatus() {
    return listingStatus;
  }

  public void setListingStatus(String listingStatus) {
    this.listingStatus = listingStatus;
  }

  public String getCoverType() {
    return coverType;
  }

  public void setCoverType(String coverType) {
    this.coverType = coverType;
  }

  public String getCoverValue() {
    return coverValue;
  }

  public void setCoverValue(String coverValue) {
    this.coverValue = coverValue;
  }

  public Integer getReadMinutes() {
    return readMinutes;
  }

  public void setReadMinutes(Integer readMinutes) {
    this.readMinutes = readMinutes;
  }

  public Integer getHeat() {
    return heat;
  }

  public void setHeat(Integer heat) {
    this.heat = heat;
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(LocalDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  public LocalDateTime getApprovedAt() {
    return approvedAt;
  }

  public void setApprovedAt(LocalDateTime approvedAt) {
    this.approvedAt = approvedAt;
  }

  public LocalDateTime getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(LocalDateTime publishedAt) {
    this.publishedAt = publishedAt;
  }
}
