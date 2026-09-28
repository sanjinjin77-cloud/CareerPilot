package com.careerpilot.ai;

import java.util.List;

public record AiInterviewContext(
        String jobTitle,
        String resumeTitle,
        String resumeContent,
        List<String> keywords,
        String question,
        String answer
) {
}
