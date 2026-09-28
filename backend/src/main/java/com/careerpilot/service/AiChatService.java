package com.careerpilot.service;

import com.careerpilot.dto.AiChatRequest;
import com.careerpilot.entity.ChatMessage;
import com.careerpilot.vo.AiChatResponse;

import java.util.List;

public interface AiChatService {

    AiChatResponse chat(Long userId, AiChatRequest request);

    List<ChatMessage> listMessages(Long userId);
}
