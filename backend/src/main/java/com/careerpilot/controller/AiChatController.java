package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.dto.AiChatRequest;
import com.careerpilot.entity.ChatMessage;
import com.careerpilot.service.AiChatService;
import com.careerpilot.vo.AiChatResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return Result.success(aiChatService.chat(request));
    }

    @GetMapping("/messages")
    public Result<List<ChatMessage>> messages(@RequestParam(name = "userId", required = false) Long userId) {
        return Result.success(aiChatService.listMessages(userId));
    }
}
