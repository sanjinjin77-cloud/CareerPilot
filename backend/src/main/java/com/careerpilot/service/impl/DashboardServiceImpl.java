package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.dto.DashboardSnapshot;
import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.entity.InterviewRecord;
import com.careerpilot.entity.JdAnalysis;
import com.careerpilot.entity.LearningPlan;
import com.careerpilot.entity.LearningTask;
import com.careerpilot.entity.ResumeDocument;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.entity.TargetOffer;
import com.careerpilot.entity.UserProfile;
import com.careerpilot.service.AbilityAnalysisService;
import com.careerpilot.service.CareerPlanService;
import com.careerpilot.service.DashboardService;
import com.careerpilot.service.InterviewRecordService;
import com.careerpilot.service.JdAnalysisService;
import com.careerpilot.service.LearningPlanService;
import com.careerpilot.service.LearningTaskService;
import com.careerpilot.service.ResumeDocumentService;
import com.careerpilot.service.ResumeMaterialService;
import com.careerpilot.service.TargetOfferService;
import com.careerpilot.service.UserProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final List<String> MILESTONES = List.of(
            "职业认知", "技能学习", "项目积累", "简历优化", "模拟面试", "Offer 冲刺"
    );

    private final UserProfileService userProfileService;
    private final CareerPlanService careerPlanService;
    private final AbilityAnalysisService abilityAnalysisService;
    private final LearningPlanService learningPlanService;
    private final LearningTaskService learningTaskService;
    private final ResumeMaterialService resumeMaterialService;
    private final ResumeDocumentService resumeDocumentService;
    private final JdAnalysisService jdAnalysisService;
    private final InterviewRecordService interviewRecordService;
    private final TargetOfferService targetOfferService;

    public DashboardServiceImpl(UserProfileService userProfileService,
                                CareerPlanService careerPlanService,
                                AbilityAnalysisService abilityAnalysisService,
                                LearningPlanService learningPlanService,
                                LearningTaskService learningTaskService,
                                ResumeMaterialService resumeMaterialService,
                                ResumeDocumentService resumeDocumentService,
                                JdAnalysisService jdAnalysisService,
                                InterviewRecordService interviewRecordService,
                                TargetOfferService targetOfferService) {
        this.userProfileService = userProfileService;
        this.careerPlanService = careerPlanService;
        this.abilityAnalysisService = abilityAnalysisService;
        this.learningPlanService = learningPlanService;
        this.learningTaskService = learningTaskService;
        this.resumeMaterialService = resumeMaterialService;
        this.resumeDocumentService = resumeDocumentService;
        this.jdAnalysisService = jdAnalysisService;
        this.interviewRecordService = interviewRecordService;
        this.targetOfferService = targetOfferService;
    }

    @Override
    public DashboardSnapshot getSnapshot(Long userId) {
        UserProfile profile = latestProfile(userId);
        CareerPlan careerPlan = latestCareerPlan(userId);
        List<AbilityAnalysis> abilities = latestAbilities(userId);
        AbilityAnalysis ability = abilities.isEmpty() ? null : abilities.get(0);
        LearningPlan learningPlan = latestLearningPlan(userId);
        List<LearningTask> tasks = learningPlan == null ? List.of() : tasks(userId, learningPlan.getId());
        List<JdAnalysis> jdAnalyses = latestJdAnalyses(userId);
        TargetOffer targetOffer = currentTargetOffer(userId);
        ResumeDocument finalResume = finalResume(userId);

        long materialCount = resumeMaterialService.count(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, userId));
        long materializedTaskCount = resumeMaterialService.count(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, userId)
                .isNotNull(ResumeMaterial::getSourceTaskId));
        long interviewCount = interviewRecordService.count(new LambdaQueryWrapper<InterviewRecord>()
                .eq(InterviewRecord::getUserId, userId));
        long completedTasks = tasks.stream().filter(task -> "done".equals(task.getStatus())).count();
        long pendingTasks = tasks.size() - completedTasks;
        int progress = tasks.isEmpty()
                ? value(learningPlan == null ? null : learningPlan.getProgress())
                : (int) Math.round(completedTasks * 100.0 / tasks.size());

        int matchScore = matchScore(jdAnalyses, abilities);
        int matchTrend = matchTrend(jdAnalyses, abilities);
        int milestoneStep = milestoneStep(profile, careerPlan, ability, materialCount, jdAnalyses, interviewCount);
        LearningTask focusTask = focusTask(tasks);
        Suggestion suggestion = suggestion(focusTask, ability, careerPlan);

        return new DashboardSnapshot(
                text(profile == null ? null : profile.getGrade(), "待完善"),
                text(profile == null ? null : profile.getCurrentStage(),
                        text(careerPlan == null ? null : careerPlan.getCurrentStage(), "职业探索")),
                text(profile == null ? null : profile.getTargetRole(),
                        text(ability == null ? null : ability.getTargetRole(), "待确定目标岗位")),
                text(careerPlan == null ? null : careerPlan.getCurrentLevel(),
                        text(profile == null ? null : profile.getProfileSummary(), "完善画像后生成阶段分析")),
                matchScore,
                matchTrend,
                Math.max(0, Math.min(100, progress)),
                tasks.size(),
                completedTasks,
                pendingTasks,
                materialCount,
                materializedTaskCount,
                jdAnalysisService.count(new LambdaQueryWrapper<JdAnalysis>().eq(JdAnalysis::getUserId, userId)),
                interviewCount,
                text(targetOffer == null ? null : targetOffer.getJobTitle(), ""),
                text(targetOffer == null ? null : targetOffer.getCompanyName(), ""),
                clamp(targetOffer == null ? null : targetOffer.getMatchScore()),
                text(finalResume == null ? null : finalResume.getTitle(), ""),
                text(focusTask == null ? null : focusTask.getTitle(), ""),
                text(focusTask == null ? null : focusTask.getTaskType(), ""),
                focusTask == null || focusTask.getDueDate() == null ? "" : focusTask.getDueDate().toString(),
                milestoneStep,
                MILESTONES.size(),
                MILESTONES,
                suggestion.title(),
                suggestion.content(),
                suggestion.path()
        );
    }

    private UserProfile latestProfile(Long userId) {
        return userProfileService.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .orderByDesc(UserProfile::getUpdatedAt)
                .orderByDesc(UserProfile::getId)
                .last("LIMIT 1"));
    }

    private CareerPlan latestCareerPlan(Long userId) {
        return careerPlanService.getOne(new LambdaQueryWrapper<CareerPlan>()
                .eq(CareerPlan::getUserId, userId)
                .orderByDesc(CareerPlan::getUpdatedAt)
                .orderByDesc(CareerPlan::getId)
                .last("LIMIT 1"));
    }

    private List<AbilityAnalysis> latestAbilities(Long userId) {
        return abilityAnalysisService.list(new LambdaQueryWrapper<AbilityAnalysis>()
                .eq(AbilityAnalysis::getUserId, userId)
                .orderByDesc(AbilityAnalysis::getUpdatedAt)
                .orderByDesc(AbilityAnalysis::getId)
                .last("LIMIT 2"));
    }

    private LearningPlan latestLearningPlan(Long userId) {
        return learningPlanService.getOne(new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, "active")
                .orderByDesc(LearningPlan::getUpdatedAt)
                .orderByDesc(LearningPlan::getId)
                .last("LIMIT 1"));
    }

    private List<LearningTask> tasks(Long userId, Long planId) {
        return learningTaskService.list(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, userId)
                .eq(LearningTask::getPlanId, planId));
    }

    private List<JdAnalysis> latestJdAnalyses(Long userId) {
        return jdAnalysisService.list(new LambdaQueryWrapper<JdAnalysis>()
                .eq(JdAnalysis::getUserId, userId)
                .orderByDesc(JdAnalysis::getUpdatedAt)
                .orderByDesc(JdAnalysis::getId)
                .last("LIMIT 2"));
    }

    private TargetOffer currentTargetOffer(Long userId) {
        return targetOfferService.getOne(new LambdaQueryWrapper<TargetOffer>()
                .eq(TargetOffer::getUserId, userId)
                .eq(TargetOffer::getStatus, "active")
                .orderByDesc(TargetOffer::getUpdatedAt)
                .orderByDesc(TargetOffer::getId)
                .last("LIMIT 1"));
    }

    private ResumeDocument finalResume(Long userId) {
        return resumeDocumentService.getOne(new LambdaQueryWrapper<ResumeDocument>()
                .eq(ResumeDocument::getUserId, userId)
                .eq(ResumeDocument::getStatus, "final")
                .orderByDesc(ResumeDocument::getUpdatedAt)
                .orderByDesc(ResumeDocument::getId)
                .last("LIMIT 1"));
    }

    private int matchScore(List<JdAnalysis> jdAnalyses, List<AbilityAnalysis> abilities) {
        if (!jdAnalyses.isEmpty() && jdAnalyses.get(0).getMatchScore() != null) {
            return clamp(jdAnalyses.get(0).getMatchScore());
        }
        return abilities.isEmpty() ? 0 : clamp(abilities.get(0).getMatchScore());
    }

    private int matchTrend(List<JdAnalysis> jdAnalyses, List<AbilityAnalysis> abilities) {
        if (jdAnalyses.size() > 1) {
            return clampTrend(value(jdAnalyses.get(0).getMatchScore()) - value(jdAnalyses.get(1).getMatchScore()));
        }
        if (abilities.size() > 1) {
            return clampTrend(value(abilities.get(0).getMatchScore()) - value(abilities.get(1).getMatchScore()));
        }
        return 0;
    }

    private int milestoneStep(UserProfile profile, CareerPlan plan, AbilityAnalysis ability, long materialCount,
                              List<JdAnalysis> jdAnalyses, long interviewCount) {
        int step = 0;
        if (profile != null) step = 1;
        if (step == 1 && plan != null) step = 2;
        if (step == 2 && ability != null) step = 3;
        if (step == 3 && materialCount > 0) step = 4;
        if (step == 4 && !jdAnalyses.isEmpty()) step = 5;
        if (step == 5 && interviewCount > 0) step = 6;
        return step;
    }

    private LearningTask focusTask(List<LearningTask> tasks) {
        return tasks.stream()
                .filter(task -> "todo".equals(task.getStatus()))
                .sorted(Comparator.comparingInt(this::priorityRank)
                        .thenComparing(LearningTask::getDueDate, Comparator.nullsLast(LocalDate::compareTo)))
                .findFirst()
                .orElse(null);
    }

    private int priorityRank(LearningTask task) {
        if ("high".equals(task.getPriority())) return 0;
        if ("medium".equals(task.getPriority())) return 1;
        return 2;
    }

    private Suggestion suggestion(LearningTask task, AbilityAnalysis ability, CareerPlan plan) {
        if (task != null) {
            return new Suggestion(
                    text(task.getTitle(), "完成下一项成长任务"),
                    text(task.getDescription(), "完成任务后及时记录成果，并沉淀为简历素材。"),
                    taskPath(task.getTaskType())
            );
        }
        if (ability != null && ability.getSuggestions() != null && !ability.getSuggestions().isBlank()) {
            return new Suggestion("继续补齐岗位能力短板", ability.getSuggestions(), "/ability");
        }
        if (plan != null && plan.getShortTermGoal() != null && !plan.getShortTermGoal().isBlank()) {
            return new Suggestion("推进近期职业目标", plan.getShortTermGoal(), "/learning-plan");
        }
        return new Suggestion("完善你的职业成长档案", "先补充用户画像，系统会据此生成能力分析和学习任务。", "/profile");
    }

    private String taskPath(String taskType) {
        if ("resume".equals(taskType)) return "/resume/materials";
        if ("interview".equals(taskType)) return "/interview";
        return "/learning-plan";
    }

    private int clamp(Integer score) {
        return Math.max(0, Math.min(100, value(score)));
    }

    private int clampTrend(int trend) {
        return Math.max(-100, Math.min(100, trend));
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private record Suggestion(String title, String content, String path) {
    }
}
