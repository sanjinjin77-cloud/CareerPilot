package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.LearningTask;
import com.careerpilot.mapper.LearningTaskMapper;
import com.careerpilot.service.LearningTaskService;
import org.springframework.stereotype.Service;

@Service
public class LearningTaskServiceImpl extends ServiceImpl<LearningTaskMapper, LearningTask> implements LearningTaskService {
}
