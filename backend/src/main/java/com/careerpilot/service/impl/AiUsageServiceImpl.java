package com.careerpilot.service.impl;

import com.careerpilot.config.AiProperties;
import com.careerpilot.common.AiQuotaExceededException;
import com.careerpilot.common.UserContext;
import com.careerpilot.entity.AiCallRecord;
import com.careerpilot.mapper.AiCallRecordMapper;
import com.careerpilot.mapper.AiDailyUsageMapper;
import com.careerpilot.service.AiUsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class AiUsageServiceImpl implements AiUsageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiUsageServiceImpl.class);

    private final AiProperties properties;
    private final AiDailyUsageMapper dailyUsageMapper;
    private final AiCallRecordMapper callRecordMapper;

    public AiUsageServiceImpl(AiProperties properties,
                              AiDailyUsageMapper dailyUsageMapper,
                              AiCallRecordMapper callRecordMapper) {
        this.properties = properties;
        this.dailyUsageMapper = dailyUsageMapper;
        this.callRecordMapper = callRecordMapper;
    }

    @Override
    public <T> AiExecution<T> execute(Long userId,
                                      String feature,
                                      boolean available,
                                      String model,
                                      Supplier<Optional<T>> llmCall,
                                      Supplier<T> fallback) {
        Long normalizedUserId = UserContext.requireValidUserId(userId);
        if (!available) {
            record(normalizedUserId, feature, "local_fallback", "", true, 0L, "AI provider unavailable");
            return new AiExecution<>(fallback.get(), "local_fallback", "");
        }

        reserve(normalizedUserId);
        long startedAt = System.nanoTime();
        try {
            Optional<T> result = llmCall.get();
            long durationMs = elapsedMillis(startedAt);
            if (result.isPresent()) {
                record(normalizedUserId, feature, "llm", model, true, durationMs, "");
                return new AiExecution<>(result.get(), "llm", model);
            }
            record(normalizedUserId, feature, "llm", model, false, durationMs, "empty or invalid AI response");
        } catch (Exception exception) {
            long durationMs = elapsedMillis(startedAt);
            record(normalizedUserId, feature, "llm", model, false, durationMs, summarize(exception));
            LOGGER.warn("AI feature={} failed; using local fallback: {}", feature, summarize(exception));
        }

        record(normalizedUserId, feature, "local_fallback", "", true, elapsedMillis(startedAt), "llm unavailable");
        return new AiExecution<>(fallback.get(), "local_fallback", "");
    }

    private void reserve(Long userId) {
        if (!properties.isQuotaEnabled()) {
            return;
        }
        int limit = Math.max(1, properties.getDailyLimit());
        int affectedRows = dailyUsageMapper.reserveCall(userId, LocalDate.now(), limit);
        if (affectedRows == 0) {
            throw new AiQuotaExceededException(limit);
        }
    }

    private void record(Long userId, String feature, String source, String model,
                        boolean success, long durationMs, String errorSummary) {
        try {
            AiCallRecord record = new AiCallRecord();
            record.setUserId(userId);
            record.setFeature(feature);
            record.setSource(source);
            record.setModel(model == null ? "" : model);
            record.setSuccess(success);
            record.setDurationMs(Math.max(0L, durationMs));
            record.setErrorSummary(trim(errorSummary, 500));
            record.setCreatedAt(LocalDateTime.now());
            callRecordMapper.insert(record);
        } catch (Exception exception) {
            LOGGER.warn("AI usage record failed for feature={}: {}", feature, summarize(exception));
        }
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private String summarize(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return trim(exception.getClass().getSimpleName() + ": " + message, 500);
    }

    private String trim(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
