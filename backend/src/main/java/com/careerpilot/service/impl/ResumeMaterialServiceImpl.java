package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.mapper.ResumeMaterialMapper;
import com.careerpilot.service.ResumeMaterialService;
import org.springframework.stereotype.Service;

@Service
public class ResumeMaterialServiceImpl extends ServiceImpl<ResumeMaterialMapper, ResumeMaterial> implements ResumeMaterialService {
}
