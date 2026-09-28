package com.careerpilot.ai;

public record AiInterviewEvaluation(
        int technical,
        int project,
        int logic,
        int star,
        String feedback
) {
    public int averageScore() {
        return (int) Math.round((technical + project + logic + star) / 4.0);
    }
}
