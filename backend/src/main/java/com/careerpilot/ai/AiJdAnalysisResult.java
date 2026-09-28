package com.careerpilot.ai;

import java.util.List;

public record AiJdAnalysisResult(
        List<String> keywords,
        List<String> requirements,
        int matchScore,
        List<Long> recommendedMaterialIds,
        List<String> suggestions
) {
}
