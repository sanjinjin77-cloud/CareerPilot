package com.careerpilot.common;

public class AiQuotaExceededException extends RuntimeException {

    public AiQuotaExceededException(int dailyLimit) {
        super("今日 AI 调用次数已达到上限（" + dailyLimit + " 次），请明天再试");
    }
}
