package com.careerpilot.controller;

import com.careerpilot.dto.InterviewImprovementResult;
import com.careerpilot.entity.LearningPlan;
import com.careerpilot.entity.LearningTask;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.LearningWorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    private final LearningWorkflowService learningWorkflowService;
    private final AuthSessionService authSessionService;

    public LearningController(LearningWorkflowService learningWorkflowService,
                              AuthSessionService authSessionService) {
        this.learningWorkflowService = learningWorkflowService;
        this.authSessionService = authSessionService;
    }

    @PostMapping("/plan/generate")
    public Map<String, Object> generatePlan(HttpServletRequest request) {
        return toPlan(learningWorkflowService.generatePlan(authSessionService.currentUserId(request)));
    }

    @GetMapping("/plan/current")
    public Map<String, Object> currentPlan(HttpServletRequest request) {
        LearningPlan plan = learningWorkflowService.currentPlan(authSessionService.currentUserId(request));
        return plan == null ? null : toPlan(plan);
    }

    @GetMapping("/tasks")
    public List<Map<String, Object>> tasks(HttpServletRequest request) {
        return learningWorkflowService.listTasks(authSessionService.currentUserId(request)).stream()
                .map(this::toTask)
                .toList();
    }

    @PostMapping("/tasks/{taskId}/complete")
    public Map<String, Object> completeTask(@PathVariable("taskId") Long taskId, HttpServletRequest request) {
        return toTask(learningWorkflowService.completeTask(authSessionService.currentUserId(request), taskId));
    }

    @PostMapping("/tasks/{taskId}/material")
    public Map<String, Object> createMaterial(@PathVariable("taskId") Long taskId, HttpServletRequest request) {
        return toMaterial(learningWorkflowService.createMaterialFromTask(authSessionService.currentUserId(request), taskId));
    }

    @PostMapping("/interview-review")
    public Map<String, Object> createInterviewReviewTasks(HttpServletRequest request) {
        InterviewImprovementResult result = learningWorkflowService
                .createInterviewImprovementTasks(authSessionService.currentUserId(request));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("job_title", result.jobTitle());
        data.put("average_score", result.averageScore());
        data.put("focus_areas", result.focusAreas());
        data.put("summary", result.summary());
        data.put("tasks", result.tasks().stream().map(this::toTask).toList());
        return data;
    }

    private Map<String, Object> toPlan(LearningPlan plan) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", plan.getId());
        data.put("current_stage", plan.getCurrentStage());
        data.put("total_period", plan.getTotalPeriod());
        data.put("weekly_hours", plan.getWeeklyHours());
        data.put("goal_summary", plan.getGoalSummary());
        data.put("stage_tasks", plan.getStageTasks());
        data.put("skill_tasks", plan.getSkillTasks());
        data.put("project_tasks", plan.getProjectTasks());
        data.put("resume_tasks", plan.getResumeTasks());
        data.put("interview_tasks", plan.getInterviewTasks());
        data.put("progress", plan.getProgress());
        data.put("generation_source", plan.getGenerationSource() == null ? plan.getPlanSource() : plan.getGenerationSource());
        data.put("model", plan.getModel() == null ? "" : plan.getModel());
        return data;
    }

    private Map<String, Object> toTask(LearningTask task) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", task.getId());
        data.put("plan_id", task.getPlanId());
        data.put("task_type", task.getTaskType());
        data.put("title", task.getTitle());
        data.put("description", task.getDescription());
        data.put("priority", task.getPriority());
        data.put("status", task.getStatus());
        data.put("due_date", task.getDueDate());
        data.put("completed_at", task.getCompletedAt());
        return data;
    }

    private Map<String, Object> toMaterial(ResumeMaterial material) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", material.getId());
        data.put("type", material.getType());
        data.put("title", material.getTitle());
        data.put("original_content", material.getRawText());
        data.put("optimized_content", material.getOptimizedText());
        data.put("tags", material.getTags() == null || material.getTags().isBlank()
                ? List.of()
                : List.of(material.getTags().split("\\s*,\\s*")));
        data.put("source_task_id", material.getSourceTaskId());
        data.put("source_type", material.getSourceType());
        data.put("optimization_source", "");
        data.put("model", "");
        return data;
    }
}
