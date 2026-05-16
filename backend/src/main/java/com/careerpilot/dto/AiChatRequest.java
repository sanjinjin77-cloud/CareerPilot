package com.careerpilot.dto;

import jakarta.validation.constraints.NotBlank;

public class AiChatRequest {

    private Long userId;

    @NotBlank(message = "消息内容不能为空")
    private String message;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
