package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.common.UserContext;
import com.careerpilot.entity.JobPosition;
import com.careerpilot.entity.TargetOffer;
import com.careerpilot.mapper.TargetOfferMapper;
import com.careerpilot.service.JobPositionService;
import com.careerpilot.service.TargetOfferService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TargetOfferServiceImpl extends ServiceImpl<TargetOfferMapper, TargetOffer> implements TargetOfferService {

    private final JobPositionService jobPositionService;

    public TargetOfferServiceImpl(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }

    @Override
    public TargetOffer getCurrentTargetOffer(Long userId) {
        return getOne(new LambdaQueryWrapper<TargetOffer>()
                .eq(TargetOffer::getUserId, UserContext.requireValidUserId(userId))
                .eq(TargetOffer::getStatus, "active")
                .orderByDesc(TargetOffer::getUpdatedAt)
                .last("LIMIT 1"));
    }

    @Override
    public TargetOffer setTargetOffer(Long userId, Long jobId) {
        JobPosition job = jobPositionService.getById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("岗位不存在");
        }

        Long normalizedUserId = UserContext.requireValidUserId(userId);
        update(new LambdaUpdateWrapper<TargetOffer>()
                .eq(TargetOffer::getUserId, normalizedUserId)
                .eq(TargetOffer::getStatus, "active")
                .set(TargetOffer::getStatus, "inactive"));

        TargetOffer offer = new TargetOffer();
        offer.setUserId(normalizedUserId);
        offer.setJobId(job.getId());
        offer.setJobTitle(job.getJobTitle());
        offer.setCompanyName(job.getCompanyName());
        offer.setTargetRole(job.getJobCategory());
        offer.setTargetCity(job.getCity());
        offer.setTargetSalary(job.getSalaryMin() + "-" + job.getSalaryMax());
        offer.setRequiredSkills(job.getTechStack());
        offer.setJobRequirements(job.getJobRequirement());
        offer.setMatchScore(0);
        offer.setStatus("active");
        offer.setCreatedAt(LocalDateTime.now());
        offer.setUpdatedAt(LocalDateTime.now());
        save(offer);
        return offer;
    }

}
