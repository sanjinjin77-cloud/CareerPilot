package com.careerpilot.controller;

import com.careerpilot.dto.DashboardSnapshot;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthSessionService authSessionService;

    public DashboardController(DashboardService dashboardService, AuthSessionService authSessionService) {
        this.dashboardService = dashboardService;
        this.authSessionService = authSessionService;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary(HttpServletRequest request) {
        DashboardSnapshot snapshot = dashboardService.getSnapshot(authSessionService.currentUserId(request));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("grade", snapshot.grade());
        data.put("current_stage", snapshot.currentStage());
        data.put("target_position", snapshot.targetPosition());
        data.put("current_level", snapshot.currentLevel());
        data.put("match_score", snapshot.matchScore());
        data.put("match_trend", snapshot.matchTrend());
        data.put("learning_progress", snapshot.learningProgress());
        data.put("total_tasks", snapshot.totalTasks());
        data.put("completed_tasks", snapshot.completedTasks());
        data.put("pending_tasks", snapshot.pendingTasks());
        data.put("resume_material_count", snapshot.resumeMaterialCount());
        data.put("materialized_task_count", snapshot.materializedTaskCount());
        data.put("jd_analysis_count", snapshot.jdAnalysisCount());
        data.put("interview_count", snapshot.interviewCount());
        data.put("target_offer_title", snapshot.targetOfferTitle());
        data.put("target_offer_company", snapshot.targetOfferCompany());
        data.put("target_offer_match_score", snapshot.targetOfferMatchScore());
        data.put("final_resume_title", snapshot.finalResumeTitle());
        data.put("next_task_title", snapshot.nextTaskTitle());
        data.put("next_task_type", snapshot.nextTaskType());
        data.put("next_task_due_date", snapshot.nextTaskDueDate());
        data.put("milestone_step", snapshot.milestoneStep());
        data.put("milestone_total", snapshot.milestoneTotal());
        data.put("milestone_labels", snapshot.milestoneLabels());
        data.put("suggestion_title", snapshot.suggestionTitle());
        data.put("suggestion", snapshot.suggestion());
        data.put("suggestion_path", snapshot.suggestionPath());
        data.put("data_source", "database");
        return data;
    }
}
