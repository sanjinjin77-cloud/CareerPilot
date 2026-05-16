package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.mapper.CareerPlanMapper;
import com.careerpilot.service.CareerPlanService;
import org.springframework.stereotype.Service;

@Service
public class CareerPlanServiceImpl extends ServiceImpl<CareerPlanMapper, CareerPlan> implements CareerPlanService {
}
