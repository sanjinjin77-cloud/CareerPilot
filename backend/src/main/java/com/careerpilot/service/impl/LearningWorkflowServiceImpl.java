package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.ai.AiGrowthProfile;
import com.careerpilot.ai.AiLearningContext;
import com.careerpilot.ai.AiLearningPlanResult;
import com.careerpilot.ai.AiLearningTaskDraft;
import com.careerpilot.ai.CareerAiProvider;
import com.careerpilot.common.UserContext;
import com.careerpilot.dto.InterviewImprovementResult;
import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.InterviewRecord;
import com.careerpilot.entity.LearningPlan;
import com.careerpilot.entity.LearningTask;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.entity.UserProfile;
import com.careerpilot.service.AbilityAnalysisService;
import com.careerpilot.service.AiUsageService;
import com.careerpilot.service.InterviewRecordService;
import com.careerpilot.service.LearningPlanService;
import com.careerpilot.service.LearningTaskService;
import com.careerpilot.service.LearningWorkflowService;
import com.careerpilot.service.ResumeMaterialService;
import com.careerpilot.service.UserProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LearningWorkflowServiceImpl implements LearningWorkflowService {

    private final UserProfileService userProfileService;
    private final AbilityAnalysisService abilityAnalysisService;
    private final LearningPlanService learningPlanService;
    private final LearningTaskService learningTaskService;
    private final ResumeMaterialService resumeMaterialService;
    private final InterviewRecordService interviewRecordService;
    private final CareerAiProvider careerAiProvider;
    private final AiUsageService aiUsageService;

    public LearningWorkflowServiceImpl(UserProfileService userProfileService,
                                       AbilityAnalysisService abilityAnalysisService,
                                       LearningPlanService learningPlanService,
                                       LearningTaskService learningTaskService,
                                       ResumeMaterialService resumeMaterialService,
                                       InterviewRecordService interviewRecordService,
                                       CareerAiProvider careerAiProvider,
                                       AiUsageService aiUsageService) {
        this.userProfileService = userProfileService;
        this.abilityAnalysisService = abilityAnalysisService;
        this.learningPlanService = learningPlanService;
        this.learningTaskService = learningTaskService;
        this.resumeMaterialService = resumeMaterialService;
        this.interviewRecordService = interviewRecordService;
        this.careerAiProvider = careerAiProvider;
        this.aiUsageService = aiUsageService;
    }

    @Override
    public LearningPlan generatePlan(Long userId) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        UserProfile profile = latestProfile(normalizedUserId);
        AbilityAnalysis ability = latestAbility(normalizedUserId);
        String targetRole = text(profile == null ? null : profile.getTargetRole(), "前端开发工程师");
        String currentStage = text(profile == null ? null : profile.getCurrentStage(), "实习准备");
        String weakness = text(ability == null ? null : ability.getWeaknesses(), "岗位关键词理解, 项目量化结果, 简历表达, 面试结构化回答");
        AiUsageService.AiExecution<AiLearningPlanResult> aiExecution = aiUsageService.execute(
                normalizedUserId,
                "learning_plan",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.generateLearningPlan(learningContext(profile, ability, targetRole)),
                () -> null
        );
        AiLearningPlanResult aiResult = aiExecution.value();
        boolean generatedByAi = "llm".equals(aiExecution.source()) && aiResult != null;

        LearningPlan plan = new LearningPlan();
        plan.setUserId(normalizedUserId);
        plan.setPlanSource(aiExecution.source());
        plan.setStatus("active");
        plan.setCurrentStage(currentStage);
        plan.setTotalPeriod(generatedByAi ? aiResult.totalPeriod() : "4 周");
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusWeeks(generatedByAi ? periodWeeks(aiResult.totalPeriod()) : 4));
        plan.setWeeklyHours(generatedByAi ? aiResult.weeklyHours() : weeklyHours(profile));
        plan.setGoalSummary(generatedByAi
                ? aiResult.goalSummary()
                : "围绕" + targetRole + "补齐能力短板，并把学习成果转化为简历素材和面试表达。");
        plan.setStageTasks(generatedByAi
                ? join(aiResult.stageTasks())
                : "第 1 周：拆解岗位要求; 第 2 周：补齐核心技能; 第 3 周：完成项目实践; 第 4 周：简历优化与模拟面试");
        plan.setSkillTasks(generatedByAi ? join(aiResult.skillTasks()) : skillTasks(targetRole, weakness));
        plan.setProjectTasks(generatedByAi ? join(aiResult.projectTasks()) : projectTasks(targetRole));
        plan.setResumeTasks(generatedByAi
                ? join(aiResult.resumeTasks())
                : "优化 2 条项目经历; 提炼岗位关键词; 形成 1 版针对" + targetRole + "的简历表达");
        plan.setInterviewTasks(generatedByAi
                ? join(aiResult.interviewTasks())
                : "准备 5 个项目追问; 完成 1 次模拟面试; 根据反馈复盘表达逻辑");
        plan.setProgress(0);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        learningPlanService.save(plan);

        learningTaskService.remove(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, normalizedUserId)
                .eq(LearningTask::getStatus, "todo"));
        if (generatedByAi) {
            createTasks(normalizedUserId, plan, aiResult.tasks());
        } else {
            createTasks(normalizedUserId, plan, targetRole);
        }
        plan.setGenerationSource(aiExecution.source());
        plan.setModel(aiExecution.model());
        return plan;
    }

    @Override
    public LearningPlan currentPlan(Long userId) {
        return latestActivePlan(UserContext.requireValidUserId(userId));
    }

    @Override
    public List<LearningTask> listTasks(Long userId) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        return learningTaskService.list(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, normalizedUserId)
                .orderByAsc(LearningTask::getStatus)
                .orderByAsc(LearningTask::getDueDate)
                .orderByDesc(LearningTask::getPriority)
                .orderByDesc(LearningTask::getId));
    }

    @Override
    public LearningTask completeTask(Long userId, Long taskId) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        LearningTask task = learningTaskService.getOne(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, normalizedUserId)
                .eq(LearningTask::getId, taskId)
                .last("LIMIT 1"));
        if (task == null) {
            throw new IllegalArgumentException("学习任务不存在");
        }

        task.setStatus("done");
        task.setCompletedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        learningTaskService.updateById(task);
        updatePlanProgress(normalizedUserId, task.getPlanId());
        return task;
    }

    @Override
    public ResumeMaterial createMaterialFromTask(Long userId, Long taskId) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        LearningTask task = learningTaskService.getOne(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, normalizedUserId)
                .eq(LearningTask::getId, taskId)
                .last("LIMIT 1"));
        if (task == null) {
            throw new IllegalArgumentException("学习任务不存在");
        }
        if (!"done".equals(task.getStatus())) {
            throw new IllegalArgumentException("请先完成任务，再沉淀为简历素材");
        }

        ResumeMaterial existing = resumeMaterialService.getOne(new LambdaQueryWrapper<ResumeMaterial>()
                .eq(ResumeMaterial::getUserId, normalizedUserId)
                .eq(ResumeMaterial::getSourceTaskId, taskId)
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }

        ResumeMaterial material = new ResumeMaterial();
        material.setUserId(normalizedUserId);
        material.setSourceTaskId(task.getId());
        material.setSourceType("learning_task");
        material.setType(materialType(task.getTaskType()));
        material.setTitle(task.getTitle());
        material.setRawText(rawMaterial(task));
        material.setOptimizedText(optimizedMaterial(task));
        material.setTags(materialTags(task));
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(LocalDateTime.now());
        resumeMaterialService.save(material);
        return material;
    }

    @Override
    public InterviewImprovementResult createInterviewImprovementTasks(Long userId) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        List<InterviewRecord> records = interviewRecordService.list(new LambdaQueryWrapper<InterviewRecord>()
                .eq(InterviewRecord::getUserId, normalizedUserId)
                .orderByDesc(InterviewRecord::getCreatedAt)
                .orderByDesc(InterviewRecord::getId)
                .last("LIMIT 5"));
        if (records.isEmpty()) {
            throw new IllegalArgumentException("请先完成至少一次模拟面试，再生成提升任务");
        }

        int averageScore = (int) Math.round(records.stream()
                .map(InterviewRecord::getScore)
                .filter(score -> score != null)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0));
        InterviewRecord latest = records.get(0);
        String jobTitle = text(latest.getInterviewType(), "目标岗位");
        List<String> focusAreas = improvementFocusAreas(averageScore);
        LearningPlan plan = latestActivePlan(normalizedUserId);
        if (plan == null) {
            plan = generatePlan(normalizedUserId);
        }

        List<LearningTask> tasks = List.of(
                findOrCreateImprovementTask(normalizedUserId, plan.getId(), "interview",
                        "重写最近一次面试回答",
                        "根据最近反馈重写回答，使用 STAR 结构明确背景、任务、行动和结果。复盘提示："
                                + text(latest.getFeedback(), "补充完整项目证据。"), "high", 2),
                findOrCreateImprovementTask(normalizedUserId, plan.getId(), "resume",
                        "补充项目量化结果",
                        "回到定制简历，为相关项目补充至少 2 个可验证结果，例如性能、效率、质量或用户价值提升。", "high", 4),
                findOrCreateImprovementTask(normalizedUserId, plan.getId(), "skill",
                        "准备" + jobTitle + "技术追问题库",
                        "围绕最近面试暴露的短板，整理 5 个岗位技术追问并完成书面回答。", "medium", 7)
        );
        updatePlanProgress(normalizedUserId, plan.getId());

        String summary = "最近 " + records.size() + " 次面试平均得分 " + averageScore
                + " 分，下一阶段优先提升：" + String.join("、", focusAreas) + "。";
        return new InterviewImprovementResult(jobTitle, averageScore, focusAreas, summary, tasks);
    }

    private void createTasks(Long userId, LearningPlan plan, String targetRole) {
        saveTask(userId, plan.getId(), "skill", "拆解" + targetRole + "岗位关键词",
                "选择 1 个目标 JD，提取技能、项目、工具和软素质要求。", "high", 3);
        saveTask(userId, plan.getId(), "skill", "完成核心技能补齐",
                "围绕能力分析中的最高频短板完成一次集中学习，并记录学习笔记。", "high", 7);
        saveTask(userId, plan.getId(), "project", "重构一个可展示项目模块",
                "把现有项目中的一个模块补充技术亮点，例如状态管理、异常处理或组件拆分。", "medium", 14);
        saveTask(userId, plan.getId(), "resume", "沉淀 2 条简历素材",
                "将学习和项目实践转化为职责、动作、结果结构清晰的简历描述。", "medium", 21);
        saveTask(userId, plan.getId(), "interview", "完成一次模拟面试复盘",
                "围绕目标岗位回答项目追问，并根据反馈调整表达。", "high", 28);
    }

    private void createTasks(Long userId, LearningPlan plan, List<AiLearningTaskDraft> tasks) {
        tasks.forEach(task -> saveTask(
                userId,
                plan.getId(),
                task.type(),
                task.title(),
                task.description(),
                task.priority(),
                task.dueInDays()
        ));
    }

    private void saveTask(Long userId, Long planId, String type, String title, String description, String priority, int days) {
        LearningTask task = new LearningTask();
        task.setUserId(userId);
        task.setPlanId(planId);
        task.setTaskType(type);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus("todo");
        task.setDueDate(LocalDate.now().plusDays(days));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        learningTaskService.save(task);
    }

    private LearningTask findOrCreateImprovementTask(Long userId, Long planId, String type, String title,
                                                      String description, String priority, int days) {
        LearningTask existing = learningTaskService.getOne(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, userId)
                .eq(LearningTask::getPlanId, planId)
                .eq(LearningTask::getTitle, title)
                .eq(LearningTask::getStatus, "todo")
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }

        LearningTask task = new LearningTask();
        task.setUserId(userId);
        task.setPlanId(planId);
        task.setTaskType(type);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus("todo");
        task.setDueDate(LocalDate.now().plusDays(days));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        learningTaskService.save(task);
        return task;
    }

    private LearningPlan latestActivePlan(Long userId) {
        return learningPlanService.getOne(new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, "active")
                .orderByDesc(LearningPlan::getUpdatedAt)
                .orderByDesc(LearningPlan::getId)
                .last("LIMIT 1"));
    }

    private List<String> improvementFocusAreas(int averageScore) {
        if (averageScore < 75) {
            return List.of("STAR 结构", "项目证据", "结果量化");
        }
        if (averageScore < 85) {
            return List.of("结果量化", "岗位关键词", "技术深度");
        }
        return List.of("高压追问", "表达精炼", "差异化亮点");
    }

    private void updatePlanProgress(Long userId, Long planId) {
        if (planId == null) {
            return;
        }
        List<LearningTask> tasks = learningTaskService.list(new LambdaQueryWrapper<LearningTask>()
                .eq(LearningTask::getUserId, userId)
                .eq(LearningTask::getPlanId, planId));
        if (tasks.isEmpty()) {
            return;
        }

        long done = tasks.stream().filter(task -> "done".equals(task.getStatus())).count();
        LearningPlan plan = learningPlanService.getById(planId);
        if (plan != null) {
            plan.setProgress((int) Math.round(done * 100.0 / tasks.size()));
            plan.setUpdatedAt(LocalDateTime.now());
            learningPlanService.updateById(plan);
        }
    }

    private String materialType(String taskType) {
        if ("project".equals(taskType)) {
            return "project";
        }
        if ("skill".equals(taskType)) {
            return "skill_growth";
        }
        if ("interview".equals(taskType)) {
            return "interview_review";
        }
        if ("resume".equals(taskType)) {
            return "resume_refinement";
        }
        return "growth_record";
    }

    private String rawMaterial(LearningTask task) {
        return "Task: " + text(task.getTitle(), "Growth task")
                + "\nType: " + text(task.getTaskType(), "growth")
                + "\nDescription: " + text(task.getDescription(), "")
                + "\nCompleted at: " + (task.getCompletedAt() == null ? LocalDateTime.now() : task.getCompletedAt());
    }

    private String optimizedMaterial(LearningTask task) {
        String title = text(task.getTitle(), "growth task");
        String description = text(task.getDescription(), "completed a career growth task");
        if ("project".equals(task.getTaskType())) {
            return "围绕「" + title
                    + "」完成可展示项目模块改进，覆盖需求拆解、实现细节和项目表达沉淀。"
                    + "任务摘要：" + description;
        }
        if ("skill".equals(task.getTaskType())) {
            return "围绕「" + title
                    + "」完成专项技能提升，提取目标岗位要求，练习核心知识点，并产出可复用学习笔记。"
                    + "任务摘要：" + description;
        }
        if ("interview".equals(task.getTaskType())) {
            return "围绕「" + title
                    + "」完成模拟面试复盘，使用更清晰的问题、行动、结果结构优化项目表达，并明确后续提升点。"
                    + "任务摘要：" + description;
        }
        return "完成职业成长任务「" + title
                + "」，将学习产出转化为行动和结果清晰的简历证据。"
                + "任务摘要：" + description;
    }

    private String materialTags(LearningTask task) {
        return String.join(", ", List.of(
                "learning-task",
                text(task.getTaskType(), "growth"),
                text(task.getPriority(), "medium")
        ));
    }

    private UserProfile latestProfile(Long userId) {
        return userProfileService.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .orderByDesc(UserProfile::getUpdatedAt)
                .orderByDesc(UserProfile::getId)
                .last("LIMIT 1"));
    }

    private AbilityAnalysis latestAbility(Long userId) {
        return abilityAnalysisService.getOne(new LambdaQueryWrapper<AbilityAnalysis>()
                .eq(AbilityAnalysis::getUserId, userId)
                .orderByDesc(AbilityAnalysis::getUpdatedAt)
                .orderByDesc(AbilityAnalysis::getId)
                .last("LIMIT 1"));
    }

    private String skillTasks(String targetRole, String weakness) {
        return "围绕" + targetRole + "补齐短板：" + weakness + "；每周输出学习笔记和可复盘成果。";
    }

    private String projectTasks(String targetRole) {
        return "选择一个与" + targetRole + "相关的项目模块，完成一次可展示的功能增强或重构。";
    }

    private AiLearningContext learningContext(UserProfile profile, AbilityAnalysis ability, String targetRole) {
        return new AiLearningContext(
                growthProfile(profile, targetRole),
                ability == null || ability.getMatchScore() == null ? 0 : ability.getMatchScore(),
                split(ability == null ? null : ability.getStrengths(), ","),
                split(ability == null ? null : ability.getWeaknesses(), ","),
                split(ability == null ? null : ability.getPriorities(), ">"),
                parseScores(ability == null ? null : ability.getSkillScores())
        );
    }

    private AiGrowthProfile growthProfile(UserProfile profile, String targetRole) {
        return new AiGrowthProfile(
                targetRole,
                value(profile == null ? null : profile.getGrade()),
                value(profile == null ? null : profile.getEducationLevel()),
                value(profile == null ? null : profile.getMajor()),
                value(profile == null ? null : profile.getCurrentStage()),
                value(profile == null ? null : profile.getSkills()),
                value(profile == null ? null : profile.getWeakSkills()),
                value(profile == null ? null : profile.getProjectExperiences()),
                value(profile == null ? null : profile.getCompetitionExperiences()),
                value(profile == null ? null : profile.getInternshipExperiences()),
                value(profile == null ? null : profile.getCertificates()),
                weeklyHours(profile)
        );
    }

    private List<String> split(String value, String delimiter) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(value.split(java.util.regex.Pattern.quote(delimiter)))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private Map<String, Integer> parseScores(String value) {
        Map<String, Integer> scores = new LinkedHashMap<>();
        split(value, ",").forEach(item -> {
            String[] parts = item.split(":", 2);
            if (parts.length == 2) {
                try {
                    scores.put(parts[0].trim(), Math.max(0, Math.min(100, Integer.parseInt(parts[1].trim()))));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed historical score entries.
                }
            }
        });
        return scores;
    }

    private String join(List<String> values) {
        return String.join("; ", values);
    }

    private int weeklyHours(UserProfile profile) {
        return profile == null || profile.getWeeklyLearningHours() == null
                ? 8
                : Math.max(1, profile.getWeeklyLearningHours());
    }

    private int periodWeeks(String totalPeriod) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(text(totalPeriod, "4 周"));
        return matcher.find() ? Math.max(1, Math.min(52, Integer.parseInt(matcher.group(1)))) : 4;
    }

    private String value(String value) {
        return value == null ? "" : value.trim();
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
