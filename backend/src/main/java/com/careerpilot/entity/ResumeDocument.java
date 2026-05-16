package com.careerpilot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("resume_document")
public class ResumeDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetOfferId;
    private Long templateId;
    private String title;
    private String sourceType;
    private String originalFileUrl;
    private String content;
    private String optimizedContent;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTargetOfferId() { return targetOfferId; }
    public void setTargetOfferId(Long targetOfferId) { this.targetOfferId = targetOfferId; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getOriginalFileUrl() { return originalFileUrl; }
    public void setOriginalFileUrl(String originalFileUrl) { this.originalFileUrl = originalFileUrl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getOptimizedContent() { return optimizedContent; }
    public void setOptimizedContent(String optimizedContent) { this.optimizedContent = optimizedContent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
