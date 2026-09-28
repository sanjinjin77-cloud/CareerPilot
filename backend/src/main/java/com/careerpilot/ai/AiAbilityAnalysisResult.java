package com.careerpilot.ai;

import java.util.List;
import java.util.Map;

public record AiAbilityAnalysisResult(
        int matchScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> priorities,
        Map<String, Integer> skillScores,
        String suggestion
) {
}
