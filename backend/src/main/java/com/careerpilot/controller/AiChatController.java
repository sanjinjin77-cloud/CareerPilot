package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.dto.AiChatRequest;
import com.careerpilot.entity.ChatMessage;
import com.careerpilot.service.AiChatService;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.vo.AiChatResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;
    private final AuthSessionService authSessionService;

    public AiChatController(AiChatService aiChatService, AuthSessionService authSessionService) {
        this.aiChatService = aiChatService;
        this.authSessionService = authSessionService;
    }

    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request,
                                       HttpServletRequest httpRequest) {
        return Result.success(aiChatService.chat(authSessionService.currentUserId(httpRequest), request));
    }

    @GetMapping("/messages")
    public Result<List<ChatMessage>> messages(HttpServletRequest request) {
        return Result.success(aiChatService.listMessages(authSessionService.currentUserId(request)));
    }
}
