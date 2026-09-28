package com.careerpilot.ai;

public record AiResumeMaterial(
        Long id,
        String title,
        String type,
        String content,
        String tags
) {
}
