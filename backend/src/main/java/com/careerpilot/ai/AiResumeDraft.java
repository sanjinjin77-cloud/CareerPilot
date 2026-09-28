package com.careerpilot.ai;

import java.util.List;

public record AiResumeDraft(
        String content,
        String optimizedContent,
        List<String> keywords,
        List<Long> matchedMaterialIds
) {
}
