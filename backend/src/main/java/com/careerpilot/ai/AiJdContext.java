package com.careerpilot.ai;

import java.util.List;

public record AiJdContext(
        String jobTitle,
        String companyName,
        String jdContent,
        List<AiResumeMaterial> materials
) {
}
