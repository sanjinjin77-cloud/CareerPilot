package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.ai.AiAnalysisResult;
import com.careerpilot.ai.LargeModelClient;
import com.careerpilot.dto.AiChatRequest;
import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.entity.ChatMessage;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.entity.UserProfile;
import com.careerpilot.service.AbilityAnalysisService;
import com.careerpilot.service.AiChatService;
import com.careerpilot.service.CareerPlanService;
import com.careerpilot.service.ChatMessageService;
import com.careerpilot.service.ResumeMaterialService;
import com.careerpilot.service.UserProfileService;
import com.careerpilot.vo.AiChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Long DEFAULT_USER_ID = 1L;

    private final ChatMessageService chatMessageService;
    private final UserProfileService userProfileService;
    private final CareerPlanService careerPlanService;
    private final AbilityAnalysisService abilityAnalysisService;
    private final ResumeMaterialService resumeMaterialService;
    private final LargeModelClient largeModelClient;

    public AiChatServiceImpl(ChatMessageService chatMessageService,
                             UserProfileService userProfileService,
                             CareerPlanService careerPlanService,
                             AbilityAnalysisService abilityAnalysisService,
                             ResumeMaterialService resumeMaterialService,
                             LargeModelClient largeModelClient) {
        this.chatMessageService = chatMessageService;
        this.userProfileService = userProfileService;
        this.careerPlanService = careerPlanService;
        this.abilityAnalysisService = abilityAnalysisService;
        this.resumeMaterialService = resumeMaterialService;
        this.largeModelClient = largeModelClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiChatResponse chat(AiChatRequest request) {
        Long userId = normalizeUserId(request.getUserId());
        String message = request.getMessage().trim();

        saveMessage(userId, "user", message);

        AiAnalysisResult analysisResult = largeModelClient.analyze(message);
        UserProfile profile = upsertUserProfile(userId, analysisResult);
        CareerPlan careerPlan = upsertCareerPlan(userId, analysisResult);
        AbilityAnalysis abilityAnalysis = upsertAbilityAnalysis(userId, analysisResult);
        ResumeMaterial resumeMaterial = saveResumeMaterialIfNeeded(userId, analysisResult);

        saveMessage(userId, "assistant", analysisResult.getReply());

        AiChatResponse response = new AiChatResponse();
        response.setReply(analysisResult.getReply());
        response.setProfile(profile);
        response.setCareerPlan(careerPlan);
        response.setAbilityAnalysis(abilityAnalysis);
        response.setResumeMaterial(resumeMaterial);
        response.setTasks(analysisResult.getTasks());
        return response;
    }

    @Override
    public List<ChatMessage> listMessages(Long userId) {
        Long normalizedUserId = normalizeUserId(userId);
        return chatMessageService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getUserId, normalizedUserId)
                .orderByAsc(ChatMessage::getCreatedAt)
                .orderByAsc(ChatMessage::getId));
    }

    private void saveMessage(Long userId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        chatMessageService.save(message);
    }

    private UserProfile upsertUserProfile(Long userId, AiAnalysisResult result) {
        UserProfile profile = userProfileService.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .last("LIMIT 1"));
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setCreatedAt(LocalDateTime.now());
        }

        if (result.hasProfileSignals()) {
            setIfHasText(result.getGrade(), profile::setGrade);
            setIfHasText(result.getMajor(), profile::setMajor);
            setIfHasText(result.getSchool(), profile::setSchool);
            setIfHasText(result.getTargetRole(), profile::setTargetRole);
            setIfHasText(result.getTargetCity(), profile::setTargetCity);
            setIfHasText(result.getCurrentStage(), profile::setCurrentStage);
            setIfHasText(result.getSkills(), profile::setSkills);
            setIfHasText(result.getWeakSkills(), profile::setWeakSkills);
            setIfHasText(result.getInterests(), profile::setInterests);
            setIfHasText(result.getCareerPreference(), profile::setCareerPreference);
            setIfHasText(result.getProfileSummary(), profile::setProfileSummary);
        }

        profile.setUpdatedAt(LocalDateTime.now());
        userProfileService.saveOrUpdate(profile);
        return profile;
    }

    private CareerPlan upsertCareerPlan(Long userId, AiAnalysisResult result) {
        CareerPlan plan = careerPlanService.getOne(new LambdaQueryWrapper<CareerPlan>()
                .eq(CareerPlan::getUserId, userId)
                .last("LIMIT 1"));
        if (plan == null) {
            plan = new CareerPlan();
            plan.setUserId(userId);
            plan.setCreatedAt(LocalDateTime.now());
        }

        if (result.hasPlanSignals()) {
            setIfHasText(result.getCurrentStage(), plan::setCurrentStage);
            setIfHasText(result.getCurrentLevel(), plan::setCurrentLevel);
            setIfHasText(result.getShortTermGoal(), plan::setShortTermGoal);
            setIfHasText(result.getThreeMonthPlan(), plan::setThreeMonthPlan);
            setIfHasText(result.getOneYearPlan(), plan::setOneYearPlan);
            setIfHasText(result.getWeeklyTasks(), plan::setWeeklyTasks);
            setIfHasText(result.getRoadmap(), plan::setRoadmap);
        }

        plan.setUpdatedAt(LocalDateTime.now());
        careerPlanService.saveOrUpdate(plan);
        return plan;
    }

    private AbilityAnalysis upsertAbilityAnalysis(Long userId, AiAnalysisResult result) {
        AbilityAnalysis analysis = abilityAnalysisService.getOne(new LambdaQueryWrapper<AbilityAnalysis>()
                .eq(AbilityAnalysis::getUserId, userId)
                .last("LIMIT 1"));
        if (analysis == null) {
            analysis = new AbilityAnalysis();
            analysis.setUserId(userId);
            analysis.setCreatedAt(LocalDateTime.now());
        }

        if (result.hasAbilitySignals()) {
            setIfHasText(result.getTargetRole(), analysis::setTargetRole);
            if (result.getMatchScore() != null) {
                analysis.setMatchScore(result.getMatchScore());
            }
            setIfHasText(result.getStrengths(), analysis::setStrengths);
            setIfHasText(result.getWeaknesses(), analysis::setWeaknesses);
            setIfHasText(result.getPriorities(), analysis::setPriorities);
            setIfHasText(result.getSkillScores(), analysis::setSkillScores);
            setIfHasText(result.getSuggestions(), analysis::setSuggestions);
        }

        analysis.setUpdatedAt(LocalDateTime.now());
        abilityAnalysisService.saveOrUpdate(analysis);
        return analysis;
    }

    private ResumeMaterial saveResumeMaterialIfNeeded(Long userId, AiAnalysisResult result) {
        if (!Boolean.TRUE.equals(result.getProjectDetected())) {
            return null;
        }

        ResumeMaterial material = new ResumeMaterial();
        material.setUserId(userId);
        material.setType("project");
        material.setTitle(result.getProjectTitle());
        material.setRawText(result.getProjectText());
        material.setOptimizedText("待后续简历优化模块生成更适合投递的表达。");
        material.setTags(result.getProjectTags());
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(LocalDateTime.now());
        resumeMaterialService.save(material);
        return material;
    }

    private Long normalizeUserId(Long userId) {
        return userId == null || userId <= 0 ? DEFAULT_USER_ID : userId;
    }

    private void setIfHasText(String value, TextSetter setter) {
        if (value != null && !value.isBlank()) {
            setter.set(value);
        }
    }

    @FunctionalInterface
    private interface TextSetter {
        void set(String value);
    }
}
