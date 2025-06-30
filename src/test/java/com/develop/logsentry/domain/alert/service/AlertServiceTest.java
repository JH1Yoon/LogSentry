package com.develop.logsentry.domain.alert.service;

import com.develop.logsentry.domain.alert.entity.TwilioSmsSender;
import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import com.develop.logsentry.domain.log.entity.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class AlertServiceTest {

    @Mock
    private TwilioSmsSender smsSender;

    private AlertService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alertService = new AlertService(smsSender, "+820000000000");
    }

    @Test
    @DisplayName("오류 로그가 오면 TwilioSmsSender를 통해 알림을 보낸다")
    void sendLogErrorAlert_shouldSendSms() {
        // Given
        LogMessageDto dto = LogMessageDto.builder()
                .logLevel(LogLevel.ERROR)
                .exceptionName("NullPointerException")
                .message("Null reference occurred")
                .timestamp(LocalDateTime.of(2023, 6, 30, 15, 0))
                .build();

        // When
        alertService.sendLogErrorAlert(dto);

        // Then
        verify(smsSender).sendSms(
                eq("+820000000000"),
                contains("🚨 [ERROR LOG 발생]")
        );

        verify(smsSender).sendSms(
                eq("+820000000000"),
                argThat(msg ->
                        msg.contains("NullPointerException") &&
                                msg.contains("Null reference occurred") &&
                                msg.contains("2023-06-30T15:00")
                )
        );
    }
}