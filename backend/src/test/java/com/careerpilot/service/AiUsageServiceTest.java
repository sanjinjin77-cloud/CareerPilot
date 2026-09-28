package com.careerpilot.service;

import com.careerpilot.config.AiProperties;
import com.careerpilot.common.AiQuotaExceededException;
import com.careerpilot.entity.AiCallRecord;
import com.careerpilot.mapper.AiCallRecordMapper;
import com.careerpilot.mapper.AiDailyUsageMapper;
import com.careerpilot.service.impl.AiUsageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiUsageServiceTest {

    @Mock
    private AiDailyUsageMapper dailyUsageMapper;

    @Mock
    private AiCallRecordMapper callRecordMapper;

    private AiProperties properties;
    private AiUsageServiceImpl service;

    @BeforeEach
    void setUp() {
        properties = new AiProperties();
        properties.setQuotaEnabled(true);
        properties.setDailyLimit(2);
        service = new AiUsageServiceImpl(properties, dailyUsageMapper, callRecordMapper);
    }

    @Test
    void successfulLlmCallIsRecordedAndReturned() {
        when(dailyUsageMapper.reserveCall(anyLong(), any(), anyInt())).thenReturn(1);

        AiUsageService.AiExecution<String> execution = service.execute(
                7L, "jd_analysis", true, "test-model",
                () -> Optional.of("result"), () -> "fallback"
        );

        assertThat(execution.value()).isEqualTo("result");
        assertThat(execution.source()).isEqualTo("llm");
        assertThat(execution.model()).isEqualTo("test-model");
        ArgumentCaptor<AiCallRecord> captor = ArgumentCaptor.forClass(AiCallRecord.class);
        verify(callRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getFeature()).isEqualTo("jd_analysis");
        assertThat(captor.getValue().getSource()).isEqualTo("llm");
        assertThat(captor.getValue().getSuccess()).isTrue();
    }

    @Test
    void emptyLlmCallUsesFallbackAndRecordsBothSources() {
        when(dailyUsageMapper.reserveCall(anyLong(), any(), anyInt())).thenReturn(1);

        AiUsageService.AiExecution<String> execution = service.execute(
                7L, "interview_question", true, "test-model",
                Optional::empty, () -> "fallback"
        );

        assertThat(execution.value()).isEqualTo("fallback");
        assertThat(execution.source()).isEqualTo("local_fallback");
        verify(callRecordMapper, org.mockito.Mockito.times(2)).insert(any(AiCallRecord.class));
    }

    @Test
    void quotaExceededBeforeLlmCall() {
        when(dailyUsageMapper.reserveCall(anyLong(), any(), anyInt())).thenReturn(0);

        assertThatThrownBy(() -> service.execute(
                7L, "career_plan", true, "test-model",
                () -> Optional.of("should-not-run"), () -> "fallback"
        )).isInstanceOf(AiQuotaExceededException.class);
        verify(callRecordMapper, never()).insert(any(AiCallRecord.class));
    }

    @Test
    void unavailableProviderUsesFallbackWithoutConsumingQuota() {
        AiUsageService.AiExecution<String> execution = service.execute(
                7L, "ability_analysis", false, "",
                () -> Optional.of("should-not-run"), () -> "fallback"
        );

        assertThat(execution.value()).isEqualTo("fallback");
        assertThat(execution.source()).isEqualTo("local_fallback");
        verify(dailyUsageMapper, never()).reserveCall(anyLong(), any(), anyInt());
        verify(callRecordMapper).insert(any(AiCallRecord.class));
    }
}
