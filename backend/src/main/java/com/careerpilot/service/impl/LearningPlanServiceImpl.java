package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.LearningPlan;
import com.careerpilot.mapper.LearningPlanMapper;
import com.careerpilot.service.LearningPlanService;
import org.springframework.stereotype.Service;

@Service
public class LearningPlanServiceImpl extends ServiceImpl<LearningPlanMapper, LearningPlan> implements LearningPlanService {
}
