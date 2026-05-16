package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.ResumeDocument;
import com.careerpilot.mapper.ResumeDocumentMapper;
import com.careerpilot.service.ResumeDocumentService;
import org.springframework.stereotype.Service;

@Service
public class ResumeDocumentServiceImpl extends ServiceImpl<ResumeDocumentMapper, ResumeDocument> implements ResumeDocumentService {
}
