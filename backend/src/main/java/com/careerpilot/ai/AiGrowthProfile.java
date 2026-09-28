package com.careerpilot.ai;

public record AiGrowthProfile(
        String targetPosition,
        String grade,
        String educationLevel,
        String major,
        String currentStage,
        String skills,
        String weakSkills,
        String projectExperiences,
        String competitionExperiences,
        String internshipExperiences,
        String certificates,
        int weeklyLearningHours
) {
}
