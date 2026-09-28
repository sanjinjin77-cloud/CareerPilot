package com.careerpilot.dto;

import com.careerpilot.entity.LearningTask;

import java.util.List;

public record InterviewImprovementResult(
        String jobTitle,
        int averageScore,
        List<String> focusAreas,
        String summary,
        List<LearningTask> tasks
) {
}
