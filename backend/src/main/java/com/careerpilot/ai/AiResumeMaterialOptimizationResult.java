package com.careerpilot.ai;

import java.util.List;

public record AiResumeMaterialOptimizationResult(
        String optimizedContent,
        List<String> tags
) {
}
