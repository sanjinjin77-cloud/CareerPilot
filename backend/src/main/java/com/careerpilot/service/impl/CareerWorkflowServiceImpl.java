package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.ai.AiAbilityAnalysisResult;
import com.careerpilot.ai.AiCareerPlanResult;
import com.careerpilot.ai.AiGrowthProfile;
import com.careerpilot.ai.CareerAiProvider;
import com.careerpilot.common.UserContext;
import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.entity.UserProfile;
import com.careerpilot.service.AbilityAnalysisService;
import com.careerpilot.service.AiUsageService;
import com.careerpilot.service.CareerPlanService;
import com.careerpilot.service.CareerWorkflowService;
import com.careerpilot.service.UserProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CareerWorkflowServiceImpl implements CareerWorkflowService {

    private final CareerPlanService careerPlanService;
    private final AbilityAnalysisService abilityAnalysisService;
    private final UserProfileService userProfileService;
    private final CareerAiProvider careerAiProvider;
    private final AiUsageService aiUsageService;

    public CareerWorkflowServiceImpl(CareerPlanService careerPlanService,
                                     AbilityAnalysisService abilityAnalysisService,
                                     UserProfileService userProfileService,
                                     CareerAiProvider careerAiProvider,
                                     AiUsageService aiUsageService) {
        this.careerPlanService = careerPlanService;
        this.abilityAnalysisService = abilityAnalysisService;
        this.userProfileService = userProfileService;
        this.careerAiProvider = careerAiProvider;
        this.aiUsageService = aiUsageService;
    }

    @Override
    public CareerPlan generatePlan(Long userId, String stage, String targetPosition) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        String normalizedStage = hasText(stage) ? stage.trim() : "实习准备";
        String normalizedTarget = hasText(targetPosition) ? targetPosition.trim() : "前端开发工程师";
        UserProfile profile = latestProfile(normalizedUserId);
        AiUsageService.AiExecution<AiCareerPlanResult> aiExecution = aiUsageService.execute(
                normalizedUserId,
                "career_plan",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.generateCareerPlan(growthProfile(profile, normalizedTarget, normalizedStage)),
                () -> null
        );
        AiCareerPlanResult aiResult = aiExecution.value();
        boolean generatedByAi = "llm".equals(aiExecution.source()) && aiResult != null;

        CareerPlan plan = new CareerPlan();
        plan.setUserId(normalizedUserId);
        plan.setCurrentStage(normalizedStage);
        plan.setCurrentLevel(generatedByAi ? aiResult.currentLevel() : stageLevel(normalizedStage));
        plan.setShortTermGoal(generatedByAi
                ? String.join("; ", aiResult.shortTermGoals())
                : shortTermGoal(normalizedStage, normalizedTarget));
        plan.setThreeMonthPlan(generatedByAi
                ? String.join("; ", aiResult.threeMonthGoals())
                : threeMonthPlan(normalizedTarget));
        plan.setOneYearPlan(generatedByAi
                ? String.join("; ", aiResult.oneYearGoals())
                : oneYearPlan(normalizedTarget));
        plan.setWeeklyTasks(generatedByAi
                ? String.join("; ", aiResult.weeklyTasks())
                : weeklyTasks(normalizedTarget));
        plan.setRoadmap(generatedByAi
                ? String.join(" -> ", aiResult.roadmap())
                : "用户画像采集 -> 职业目标分析 -> 能力差距评估 -> 学习路线规划 -> 成长任务拆解 -> 简历素材积累");
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        careerPlanService.save(plan);
        plan.setGenerationSource(aiExecution.source());
        plan.setModel(aiExecution.model());
        return plan;
    }

    @Override
    public AbilityAnalysis analyzeAbility(Long userId, String targetPosition) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        String normalizedTarget = hasText(targetPosition) ? targetPosition.trim() : "前端开发工程师";
        UserProfile profile = latestProfile(normalizedUserId);
        AiUsageService.AiExecution<AiAbilityAnalysisResult> aiExecution = aiUsageService.execute(
                normalizedUserId,
                "ability_analysis",
                careerAiProvider.isAvailable(),
                careerAiProvider.modelName(),
                () -> careerAiProvider.analyzeAbility(growthProfile(profile, normalizedTarget)),
                () -> null
        );
        AiAbilityAnalysisResult aiResult = aiExecution.value();
        boolean analyzedByAi = "llm".equals(aiExecution.source()) && aiResult != null;

        AbilityAnalysis analysis = new AbilityAnalysis();
        analysis.setUserId(normalizedUserId);
        analysis.setTargetRole(normalizedTarget);
        analysis.setMatchScore(analyzedByAi ? aiResult.matchScore() : matchScore(normalizedTarget));
        analysis.setStrengths(analyzedByAi ? String.join(", ", aiResult.strengths()) : strengths(normalizedTarget));
        analysis.setWeaknesses(analyzedByAi ? String.join(", ", aiResult.weaknesses()) : weaknesses(normalizedTarget));
        analysis.setPriorities(analyzedByAi ? String.join(" > ", aiResult.priorities()) : priorities(normalizedTarget));
        analysis.setSkillScores(analyzedByAi ? serializeScores(aiResult.skillScores()) : skillScores(normalizedTarget));
        analysis.setSuggestions(analyzedByAi
                ? aiResult.suggestion()
                : "优先围绕目标岗位补齐最高频短板，并把学习任务沉淀为可复用的简历素材。");
        analysis.setCreatedAt(LocalDateTime.now());
        analysis.setUpdatedAt(LocalDateTime.now());
        abilityAnalysisService.save(analysis);
        analysis.setAnalysisSource(aiExecution.source());
        analysis.setModel(aiExecution.model());
        return analysis;
    }

    @Override
    public CareerPlan generatePlanFromProfile(UserProfile profile) {
        return generatePlan(profile == null ? null : profile.getUserId(),
                profile == null ? "" : profile.getCurrentStage(),
                profile == null ? "" : profile.getTargetRole());
    }

    @Override
    public AbilityAnalysis analyzeAbilityFromProfile(UserProfile profile) {
        return analyzeAbility(profile == null ? null : profile.getUserId(),
                profile == null ? "" : profile.getTargetRole());
    }

    private String stageLevel(String stage) {
        if (stage.contains("大一") || stage.contains("认知")) {
            return "职业认知期，需要建立岗位地图和学习方向";
        }
        if (stage.contains("大二") || stage.contains("能力")) {
            return "能力建设期，需要积累课程项目和比赛经历";
        }
        if (stage.contains("大三") || stage.contains("实习")) {
            return "实习准备期，需要把项目经验转化为可投递素材";
        }
        if (stage.contains("大四") || stage.contains("秋招") || stage.contains("春招")) {
            return "求职冲刺期，需要围绕 JD 定制简历并进行面试训练";
        }
        return "成长规划期，需要明确目标岗位并拆解能力路径";
    }

    private String shortTermGoal(String stage, String targetPosition) {
        return "完善用户画像; 明确" + targetPosition + "岗位要求; 补齐 1-2 个关键技能短板; 沉淀 2 条可写入简历的经历";
    }

    private String threeMonthPlan(String targetPosition) {
        return "第 1 月：学习" + targetPosition + "核心技能并完成基础练习; 第 2 月：围绕目标岗位重构一个项目; 第 3 月：完成 JD 匹配、简历优化和模拟面试";
    }

    private String oneYearPlan(String targetPosition) {
        return "形成清晰职业方向，完成 2-3 个项目作品，积累实习或比赛经历，并具备独立投递" + targetPosition + "的能力";
    }

    private String weeklyTasks(String targetPosition) {
        return "完成 3 次技术学习打卡; 优化 1 条简历素材; 复盘 1 个" + targetPosition + "岗位 JD; 完成 1 次模拟面试";
    }

    private int matchScore(String targetPosition) {
        if (targetPosition.contains("前端")) {
            return 68;
        }
        if (targetPosition.contains("Java") || targetPosition.contains("后端")) {
            return 64;
        }
        if (targetPosition.contains("产品")) {
            return 61;
        }
        if (targetPosition.contains("数据")) {
            return 59;
        }
        return 60;
    }

    private String strengths(String targetPosition) {
        if (targetPosition.contains("前端")) {
            return "Vue 基础, 页面开发能力, 项目实践意识";
        }
        if (targetPosition.contains("Java") || targetPosition.contains("后端")) {
            return "Java 基础, 接口开发意识, 数据库理解";
        }
        if (targetPosition.contains("产品")) {
            return "用户视角, 需求拆解, 表达沟通";
        }
        return "学习主动性, 项目经历, 职业目标清晰";
    }

    private String weaknesses(String targetPosition) {
        if (targetPosition.contains("前端")) {
            return "TypeScript 深度, 前端工程化, 性能优化, 面试表达";
        }
        if (targetPosition.contains("Java") || targetPosition.contains("后端")) {
            return "Spring Boot 深度, 缓存与消息队列, 系统设计, 项目表达";
        }
        if (targetPosition.contains("产品")) {
            return "竞品分析, 数据分析, 原型作品, 业务复盘";
        }
        return "岗位关键词理解, 项目量化结果, 简历表达, 面试结构化回答";
    }

    private String priorities(String targetPosition) {
        if (targetPosition.contains("前端")) {
            return "TypeScript > 项目重构 > 简历优化 > 模拟面试";
        }
        if (targetPosition.contains("Java") || targetPosition.contains("后端")) {
            return "Spring Boot > 数据库设计 > 项目部署 > 模拟面试";
        }
        if (targetPosition.contains("产品")) {
            return "竞品分析 > 原型作品 > 需求文档 > 模拟面试";
        }
        return "岗位理解 > 项目补强 > 简历优化 > 模拟面试";
    }

    private String skillScores(String targetPosition) {
        if (targetPosition.contains("前端")) {
            return "Vue:78,TypeScript:55,工程化:52,表达:60";
        }
        if (targetPosition.contains("Java") || targetPosition.contains("后端")) {
            return "Java:72,Spring Boot:60,MySQL:64,系统设计:48";
        }
        if (targetPosition.contains("产品")) {
            return "需求分析:66,原型设计:58,数据分析:50,表达:72";
        }
        return "岗位理解:60,项目实践:62,简历表达:55,面试表达:58";
    }

    private UserProfile latestProfile(Long userId) {
        return userProfileService.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .orderByDesc(UserProfile::getUpdatedAt)
                .orderByDesc(UserProfile::getId)
                .last("LIMIT 1"));
    }

    private AiGrowthProfile growthProfile(UserProfile profile, String targetPosition) {
        return growthProfile(profile, targetPosition, value(profile == null ? null : profile.getCurrentStage()));
    }

    private AiGrowthProfile growthProfile(UserProfile profile, String targetPosition, String currentStage) {
        return new AiGrowthProfile(
                targetPosition,
                value(profile == null ? null : profile.getGrade()),
                value(profile == null ? null : profile.getEducationLevel()),
                value(profile == null ? null : profile.getMajor()),
                currentStage,
                value(profile == null ? null : profile.getSkills()),
                value(profile == null ? null : profile.getWeakSkills()),
                value(profile == null ? null : profile.getProjectExperiences()),
                value(profile == null ? null : profile.getCompetitionExperiences()),
                value(profile == null ? null : profile.getInternshipExperiences()),
                value(profile == null ? null : profile.getCertificates()),
                profile == null || profile.getWeeklyLearningHours() == null
                        ? 8
                        : Math.max(1, profile.getWeeklyLearningHours())
        );
    }

    private String serializeScores(Map<String, Integer> scores) {
        return scores.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(java.util.stream.Collectors.joining(","));
    }

    private String value(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
