package com.careerpilot.common;

public final class UserContext {

    private UserContext() {
    }

    public static Long requireValidUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UnauthorizedException("请先登录");
        }
        return userId;
    }
}
