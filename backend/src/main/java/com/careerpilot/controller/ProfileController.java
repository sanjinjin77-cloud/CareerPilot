package com.careerpilot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.entity.UserProfile;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.CareerWorkflowService;
import com.careerpilot.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserProfileService userProfileService;
    private final CareerWorkflowService careerWorkflowService;
    private final AuthSessionService authSessionService;

    public ProfileController(UserProfileService userProfileService,
                             CareerWorkflowService careerWorkflowService,
                             AuthSessionService authSessionService) {
        this.userProfileService = userProfileService;
        this.careerWorkflowService = careerWorkflowService;
        this.authSessionService = authSessionService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        return toFrontend(getOrCreateProfile(authSessionService.currentUserId(request)));
    }

    @PutMapping("/me")
    public Map<String, Object> update(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        UserProfile profile = getOrCreateProfile(authSessionService.currentUserId(request));
        profile.setGrade(text(payload.get("grade")));
        profile.setEducationLevel(text(payload.get("education_level")));
        profile.setSchool(text(payload.get("school_type")));
        profile.setMajor(text(payload.get("major")));
        profile.setCurrentStage(text(payload.get("current_stage")));
        profile.setTargetRole(text(payload.get("target_position")));
        profile.setTargetCity(text(payload.get("target_city")));
        profile.setIndustryPreference(text(payload.get("target_industry")));
        profile.setSkills(join(text(payload.get("languages")), text(payload.get("frameworks"))));
        profile.setProjectExperiences(text(payload.get("projects")));
        profile.setCompetitionExperiences(text(payload.get("competitions")));
        profile.setInternshipExperiences(text(payload.get("internships")));
        profile.setCertificates(text(payload.get("certificates")));
        profile.setCareerPreference(joinList(payload.get("preferences")));
        profile.setUpdatedAt(LocalDateTime.now());
        userProfileService.saveOrUpdate(profile);
        careerWorkflowService.generatePlanFromProfile(profile);
        careerWorkflowService.analyzeAbilityFromProfile(profile);
        return toFrontend(profile);
    }

    private UserProfile getOrCreateProfile(Long userId) {
        UserProfile profile = userProfileService.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .last("LIMIT 1"));
        if (profile != null) {
            return profile;
        }

        UserProfile next = defaultProfile(userId);
        userProfileService.save(next);
        return next;
    }

    private UserProfile defaultProfile(Long userId) {
        UserProfile next = new UserProfile();
        next.setUserId(userId);
        next.setGrade("大三");
        next.setEducationLevel("本科");
        next.setSchool("普通本科");
        next.setMajor("软件工程");
        next.setCurrentStage("实习准备");
        next.setTargetRole("前端开发工程师");
        next.setTargetCity("杭州");
        next.setIndustryPreference("互联网");
        next.setSkills("Vue 3, TypeScript, JavaScript, Element Plus");
        next.setProjectExperiences("课程管理系统、CareerPilot 职业成长平台");
        next.setCompetitionExperiences("互联网+ 校赛");
        next.setInternshipExperiences("暂无正式实习");
        next.setCertificates("CET-4");
        next.setCareerPreference("技术岗, 高成长, 接受异地");
        next.setCreatedAt(LocalDateTime.now());
        next.setUpdatedAt(LocalDateTime.now());
        return next;
    }

    private Map<String, Object> toFrontend(UserProfile profile) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", profile.getId());
        data.put("user_id", profile.getUserId());
        data.put("grade", value(profile.getGrade()));
        data.put("education_level", value(profile.getEducationLevel()));
        data.put("school_type", value(profile.getSchool()));
        data.put("major", value(profile.getMajor()));
        data.put("current_stage", value(profile.getCurrentStage()));
        data.put("target_position", value(profile.getTargetRole()));
        data.put("target_city", value(profile.getTargetCity()));
        data.put("target_industry", value(profile.getIndustryPreference()));
        data.put("languages", value(profile.getSkills()));
        data.put("frameworks", value(profile.getSkills()));
        data.put("projects", value(profile.getProjectExperiences()));
        data.put("competitions", value(profile.getCompetitionExperiences()));
        data.put("internships", value(profile.getInternshipExperiences()));
        data.put("english_level", "CET-4");
        data.put("certificates", value(profile.getCertificates()));
        data.put("preferences", splitPreferences(profile.getCareerPreference()));
        return data;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private String join(String left, String right) {
        if (left.isBlank()) {
            return right;
        }
        if (right.isBlank()) {
            return left;
        }
        return left + ", " + right;
    }

    private String joinList(Object value) {
        if (value instanceof List<?> list) {
            return String.join(", ", list.stream().map(Object::toString).toList());
        }
        return text(value);
    }

    private List<String> splitPreferences(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\s*,\\s*"));
    }
}
