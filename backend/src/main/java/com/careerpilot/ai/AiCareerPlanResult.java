package com.careerpilot.ai;

import java.util.List;

public record AiCareerPlanResult(
        String currentLevel,
        List<String> shortTermGoals,
        List<String> threeMonthGoals,
        List<String> oneYearGoals,
        List<String> weeklyTasks,
        List<String> roadmap
) {
}
