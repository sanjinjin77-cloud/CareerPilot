package com.careerpilot.service;

import com.careerpilot.dto.InterviewImprovementResult;
import com.careerpilot.entity.LearningPlan;
import com.careerpilot.entity.LearningTask;
import com.careerpilot.entity.ResumeMaterial;

import java.util.List;

public interface LearningWorkflowService {

    LearningPlan generatePlan(Long userId);

    LearningPlan currentPlan(Long userId);

    List<LearningTask> listTasks(Long userId);

    LearningTask completeTask(Long userId, Long taskId);

    ResumeMaterial createMaterialFromTask(Long userId, Long taskId);

    InterviewImprovementResult createInterviewImprovementTasks(Long userId);
}
