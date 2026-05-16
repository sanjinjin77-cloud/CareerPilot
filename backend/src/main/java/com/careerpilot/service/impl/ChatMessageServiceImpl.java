package com.careerpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.careerpilot.entity.ChatMessage;
import com.careerpilot.mapper.ChatMessageMapper;
import com.careerpilot.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
}
