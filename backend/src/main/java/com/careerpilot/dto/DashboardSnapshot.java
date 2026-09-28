package com.careerpilot.dto;

import java.util.List;

public record DashboardSnapshot(
        String grade,
        String currentStage,
        String targetPosition,
        String currentLevel,
        int matchScore,
        int matchTrend,
        int learningProgress,
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        long resumeMaterialCount,
        long materializedTaskCount,
        long jdAnalysisCount,
        long interviewCount,
        String targetOfferTitle,
        String targetOfferCompany,
        int targetOfferMatchScore,
        String finalResumeTitle,
        String nextTaskTitle,
        String nextTaskType,
        String nextTaskDueDate,
        int milestoneStep,
        int milestoneTotal,
        List<String> milestoneLabels,
        String suggestionTitle,
        String suggestion,
        String suggestionPath
) {
}
