package com.careerpilot.ai;

import java.util.List;

public record AiLearningPlanResult(
        String totalPeriod,
        int weeklyHours,
        String goalSummary,
        List<String> stageTasks,
        List<String> skillTasks,
        List<String> projectTasks,
        List<String> resumeTasks,
        List<String> interviewTasks,
        List<AiLearningTaskDraft> tasks
) {
}
