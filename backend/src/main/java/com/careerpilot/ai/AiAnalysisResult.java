package com.careerpilot.ai;

import java.util.ArrayList;
import java.util.List;

public class AiAnalysisResult {

    private String reply;
    private String grade;
    private String major;
    private String school;
    private String targetRole;
    private String targetCity;
    private String currentStage;
    private String currentLevel;
    private String skills;
    private String weakSkills;
    private String interests;
    private String careerPreference;
    private String profileSummary;
    private Integer matchScore;
    private String strengths;
    private String weaknesses;
    private String priorities;
    private String skillScores;
    private String suggestions;
    private String shortTermGoal;
    private String threeMonthPlan;
    private String oneYearPlan;
    private String weeklyTasks;
    private String roadmap;
    private Boolean projectDetected = false;
    private String projectTitle;
    private String projectText;
    private String projectTags;
    private List<String> tasks = new ArrayList<>();

    public boolean hasProfileSignals() {
        return hasText(grade) || hasText(major) || hasText(school) || hasText(targetRole)
                || hasText(targetCity) || hasText(skills) || hasText(weakSkills);
    }

    public boolean hasPlanSignals() {
        return hasText(currentStage) || hasText(shortTermGoal) || hasText(threeMonthPlan)
                || hasText(oneYearPlan) || hasText(weeklyTasks) || hasText(roadmap);
    }

    public boolean hasAbilitySignals() {
        return hasText(targetRole) || matchScore != null || hasText(strengths)
                || hasText(weaknesses) || hasText(priorities) || hasText(suggestions);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
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

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getPriorities() {
        return priorities;
    }

    public void setPriorities(String priorities) {
        this.priorities = priorities;
    }

    public String getSkillScores() {
        return skillScores;
    }

    public void setSkillScores(String skillScores) {
        this.skillScores = skillScores;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getShortTermGoal() {
        return shortTermGoal;
    }

    public void setShortTermGoal(String shortTermGoal) {
        this.shortTermGoal = shortTermGoal;
    }

    public String getThreeMonthPlan() {
        return threeMonthPlan;
    }

    public void setThreeMonthPlan(String threeMonthPlan) {
        this.threeMonthPlan = threeMonthPlan;
    }

    public String getOneYearPlan() {
        return oneYearPlan;
    }

    public void setOneYearPlan(String oneYearPlan) {
        this.oneYearPlan = oneYearPlan;
    }

    public String getWeeklyTasks() {
        return weeklyTasks;
    }

    public void setWeeklyTasks(String weeklyTasks) {
        this.weeklyTasks = weeklyTasks;
    }

    public String getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(String roadmap) {
        this.roadmap = roadmap;
    }

    public Boolean getProjectDetected() {
        return projectDetected;
    }

    public void setProjectDetected(Boolean projectDetected) {
        this.projectDetected = projectDetected;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectText() {
        return projectText;
    }

    public void setProjectText(String projectText) {
        this.projectText = projectText;
    }

    public String getProjectTags() {
        return projectTags;
    }

    public void setProjectTags(String projectTags) {
        this.projectTags = projectTags;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}
