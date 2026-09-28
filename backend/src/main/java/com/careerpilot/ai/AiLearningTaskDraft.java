package com.careerpilot.ai;

public record AiLearningTaskDraft(
        String type,
        String title,
        String description,
        String priority,
        int dueInDays
) {
}
