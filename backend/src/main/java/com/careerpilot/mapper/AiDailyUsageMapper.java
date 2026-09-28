package com.careerpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.careerpilot.entity.AiDailyUsage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface AiDailyUsageMapper extends BaseMapper<AiDailyUsage> {

    @Insert("""
            INSERT INTO ai_daily_usage (user_id, usage_date, call_count, created_at, updated_at)
            VALUES (#{userId}, #{usageDate}, 1, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                call_count = IF(call_count < #{dailyLimit}, call_count + 1, call_count),
                updated_at = NOW()
            """)
    int reserveCall(@Param("userId") Long userId,
                    @Param("usageDate") LocalDate usageDate,
                    @Param("dailyLimit") int dailyLimit);
}
