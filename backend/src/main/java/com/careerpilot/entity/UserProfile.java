package com.careerpilot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("user_profile")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String grade;
    private String educationLevel;
    private String major;
    private String school;
    private String targetRole;
    private String targetCity;
    private String expectedSalary;
    private String industryPreference;
    private LocalDate graduationTime;
    private Integer weeklyLearningHours;
    private String jobSearchStatus;
    private String jobPreference;
    private String currentStage;
    private String skills;
    private String weakSkills;
    private String projectExperiences;
    private String competitionExperiences;
    private String internshipExperiences;
    private String certificates;
    private String interests;
    private String careerPreference;
    private String profileSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTargetCity() {
        return targetCity;
    }

    public void setTargetCity(String targetCity) {
        this.targetCity = targetCity;
    }

    public String getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(String expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public String getIndustryPreference() {
        return industryPreference;
    }

    public void setIndustryPreference(String industryPreference) {
        this.industryPreference = industryPreference;
    }

    public LocalDate getGraduationTime() {
        return graduationTime;
    }

    public void setGraduationTime(LocalDate graduationTime) {
        this.graduationTime = graduationTime;
    }

    public Integer getWeeklyLearningHours() {
        return weeklyLearningHours;
    }

    public void setWeeklyLearningHours(Integer weeklyLearningHours) {
        this.weeklyLearningHours = weeklyLearningHours;
    }

    public String getJobSearchStatus() {
        return jobSearchStatus;
    }

    public void setJobSearchStatus(String jobSearchStatus) {
        this.jobSearchStatus = jobSearchStatus;
    }

    public String getJobPreference() {
        return jobPreference;
    }

    public void setJobPreference(String jobPreference) {
        this.jobPreference = jobPreference;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getWeakSkills() {
        return weakSkills;
    }

    public void setWeakSkills(String weakSkills) {
        this.weakSkills = weakSkills;
    }

    public String getProjectExperiences() {
        return projectExperiences;
    }

    public void setProjectExperiences(String projectExperiences) {
        this.projectExperiences = projectExperiences;
    }

    public String getCompetitionExperiences() {
        return competitionExperiences;
    }

    public void setCompetitionExperiences(String competitionExperiences) {
        this.competitionExperiences = competitionExperiences;
    }

    public String getInternshipExperiences() {
        return internshipExperiences;
    }

    public void setInternshipExperiences(String internshipExperiences) {
        this.internshipExperiences = internshipExperiences;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getCareerPreference() {
        return careerPreference;
    }

    public void setCareerPreference(String careerPreference) {
        this.careerPreference = careerPreference;
    }

    public String getProfileSummary() {
        return profileSummary;
    }

    public void setProfileSummary(String profileSummary) {
        this.profileSummary = profileSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
