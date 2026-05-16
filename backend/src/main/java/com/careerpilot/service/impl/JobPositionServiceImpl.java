package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.UserProfile;
import com.careerpilot.entity.JobPosition;
import com.careerpilot.mapper.JobPositionMapper;
import com.careerpilot.service.UserProfileService;
import com.careerpilot.service.JobPositionService;
import com.careerpilot.vo.JobMatchVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class JobPositionServiceImpl extends ServiceImpl<JobPositionMapper, JobPosition> implements JobPositionService {

    private final UserProfileService userProfileService;

    public JobPositionServiceImpl(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public List<JobPosition> filterJobs(String keyword, String category, String city, String jobType, String techStack) {
        LambdaQueryWrapper<JobPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(hasText(keyword), JobPosition::getJobTitle, keyword)
                .or(hasText(keyword), w -> w.like(JobPosition::getCompanyName, keyword)
                        .or()
                        .like(JobPosition::getJobDescription, keyword))
                .eq(hasText(category), JobPosition::getJobCategory, category)
                .eq(hasText(city), JobPosition::getCity, city)
                .eq(hasText(jobType), JobPosition::getJobType, jobType)
                .like(hasText(techStack), JobPosition::getTechStack, techStack)
                .orderByDesc(JobPosition::getPublishTime)
                .orderByDesc(JobPosition::getId);
        return list(wrapper);
    }

    @Override
    public List<JobMatchVO> matchJobsForUser(Long userId) {
        UserProfile profile = userProfileService.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId == null ? 1L : userId)
                .last("LIMIT 1"));

        List<JobMatchVO> results = new ArrayList<>();
        for (JobPosition job : list()) {
            JobMatchVO vo = new JobMatchVO();
            vo.setJob(job);
            vo.setMatchScore(calculateMatchScore(profile, job));
            vo.setMatchReasons(buildReasons(profile, job));
            results.add(vo);
        }

        results.sort(Comparator.comparing(JobMatchVO::getMatchScore).reversed());
        return results;
    }

    private int calculateMatchScore(UserProfile profile, JobPosition job) {
        int score = 45;
        if (profile == null) {
            return score;
        }

        score += contains(job.getJobTitle(), profile.getTargetRole()) ? 18 : 0;
        score += contains(job.getJobCategory(), profile.getJobPreference()) ? 12 : 0;
        score += contains(job.getCity(), profile.getTargetCity()) ? 10 : 0;
        score += skillOverlap(profile.getSkills(), job.getTechStack()) * 5;
        score -= skillOverlap(profile.getWeakSkills(), job.getTechStack()) * 2;
        score += contains(job.getEducationRequirement(), profile.getEducationLevel()) ? 5 : 0;
        return Math.max(30, Math.min(95, score));
    }

    private List<String> buildReasons(UserProfile profile, JobPosition job) {
        List<String> reasons = new ArrayList<>();
        if (profile == null) {
            reasons.add("请先完善个人画像，系统会给出更准确的岗位匹配结果。");
            return reasons;
        }
        if (contains(job.getJobTitle(), profile.getTargetRole()) || contains(job.getJobCategory(), profile.getJobPreference())) {
            reasons.add("岗位方向与个人画像中的意向岗位较接近。");
        }
        if (contains(job.getCity(), profile.getTargetCity())) {
            reasons.add("工作城市符合你的意向城市。");
        }
        int overlap = skillOverlap(profile.getSkills(), job.getTechStack());
        if (overlap > 0) {
            reasons.add("已有技能与岗位技术栈存在 " + overlap + " 项重合。");
        }
        if (reasons.isEmpty()) {
            reasons.add("该岗位可作为拓展方向，建议结合技能要求进一步评估。");
        }
        return reasons;
    }

    private int skillOverlap(String profileSkills, String jobSkills) {
        if (!hasText(profileSkills) || !hasText(jobSkills)) {
            return 0;
        }
        int count = 0;
        for (String skill : profileSkills.split("[,，、\\s]+")) {
            if (hasText(skill) && jobSkills.toLowerCase().contains(skill.toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    private boolean contains(String source, String keyword) {
        return hasText(source) && hasText(keyword) && source.contains(keyword);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
