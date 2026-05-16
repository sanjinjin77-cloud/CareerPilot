package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.InterviewRecord;
import com.careerpilot.mapper.InterviewRecordMapper;
import com.careerpilot.service.InterviewRecordService;
import org.springframework.stereotype.Service;

@Service
public class InterviewRecordServiceImpl extends ServiceImpl<InterviewRecordMapper, InterviewRecord> implements InterviewRecordService {
}
