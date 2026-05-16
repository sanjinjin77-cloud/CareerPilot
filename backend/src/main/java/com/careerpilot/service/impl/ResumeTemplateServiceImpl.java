package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.ResumeTemplate;
import com.careerpilot.mapper.ResumeTemplateMapper;
import com.careerpilot.service.ResumeTemplateService;
import org.springframework.stereotype.Service;

@Service
public class ResumeTemplateServiceImpl extends ServiceImpl<ResumeTemplateMapper, ResumeTemplate> implements ResumeTemplateService {
}
