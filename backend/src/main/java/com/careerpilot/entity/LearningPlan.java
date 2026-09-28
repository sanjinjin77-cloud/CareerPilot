package com.careerpilot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("learning_plan")
public class LearningPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetOfferId;
    private String planSource;
    private String status;
    private String currentStage;
    private String totalPeriod;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer weeklyHours;
    private String goalSummary;
    private String stageTasks;
    private String skillTasks;
    private String projectTasks;
    private String resumeTasks;
    private String interviewTasks;
    private Integer progress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableField(exist = false)
    private String generationSource;
    @TableField(exist = false)
    private String model;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTargetOfferId() { return targetOfferId; }
    public void setTargetOfferId(Long targetOfferId) { this.targetOfferId = targetOfferId; }
    public String getPlanSource() { return planSource; }
    public void setPlanSource(String planSource) { this.planSource = planSource; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public String getTotalPeriod() { return totalPeriod; }
    public void setTotalPeriod(String totalPeriod) { this.totalPeriod = totalPeriod; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }
    public String getGoalSummary() { return goalSummary; }
    public void setGoalSummary(String goalSummary) { this.goalSummary = goalSummary; }
    public String getStageTasks() { return stageTasks; }
    public void setStageTasks(String stageTasks) { this.stageTasks = stageTasks; }
    public String getSkillTasks() { return skillTasks; }
    public void setSkillTasks(String skillTasks) { this.skillTasks = skillTasks; }
    public String getProjectTasks() { return projectTasks; }
    public void setProjectTasks(String projectTasks) { this.projectTasks = projectTasks; }
    public String getResumeTasks() { return resumeTasks; }
    public void setResumeTasks(String resumeTasks) { this.resumeTasks = resumeTasks; }
    public String getInterviewTasks() { return interviewTasks; }
    public void setInterviewTasks(String interviewTasks) { this.interviewTasks = interviewTasks; }
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getGenerationSource() { return generationSource; }
    public void setGenerationSource(String generationSource) { this.generationSource = generationSource; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
