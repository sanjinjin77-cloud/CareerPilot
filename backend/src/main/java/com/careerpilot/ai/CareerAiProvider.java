package com.careerpilot.ai;

import java.util.Optional;

public interface CareerAiProvider {

    Optional<AiCareerPlanResult> generateCareerPlan(AiGrowthProfile profile);

    Optional<AiAbilityAnalysisResult> analyzeAbility(AiGrowthProfile profile);

    Optional<AiLearningPlanResult> generateLearningPlan(AiLearningContext context);

    Optional<AiJdAnalysisResult> analyzeJd(AiJdContext context);

    Optional<AiResumeDraft> customizeResume(AiJdContext context);

    Optional<AiResumeMaterialOptimizationResult> optimizeResumeMaterial(AiResumeMaterial material);

    Optional<AiInterviewQuestion> generateInterviewQuestion(AiInterviewContext context);

    Optional<AiInterviewEvaluation> evaluateInterviewAnswer(AiInterviewContext context);

    boolean isAvailable();

    String modelName();
}
