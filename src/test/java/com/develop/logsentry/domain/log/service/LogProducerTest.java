package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import com.develop.logsentry.domain.log.entity.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogProducerTest {

    private KafkaTemplate<String, LogMessageDto> kafkaTemplate;
    private LogProducer logProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        logProducer = new LogProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("sendLog() 호출 시 KafkaTemplate.send()가 정상 호출")
    void sendLog_shouldSendCorrectMessageToKafka() {
        // Given
        Long projectId = 1L;
        LogLevel logLevel = LogLevel.ERROR;
        String exceptionName = "NullPointerException";
        String errorCodeMessage = "ERR001";
        String message = "Something went wrong";
        String stackSummary = "com.example.NullPointerException: ...";
        String source = "MyService";
        String errorCategory = "VALIDATION";

        // When
        logProducer.sendLog(projectId, logLevel, exceptionName, errorCodeMessage, message, stackSummary, source, errorCategory);

        // Then
        ArgumentCaptor<LogMessageDto> captor = ArgumentCaptor.forClass(LogMessageDto.class);
        verify(kafkaTemplate, times(1)).send(eq("logs-topic"), captor.capture());

        LogMessageDto sentDto = captor.getValue();
        assertThat(sentDto.getProjectId()).isEqualTo(projectId);
        assertThat(sentDto.getLogLevel()).isEqualTo(logLevel);
        assertThat(sentDto.getExceptionName()).isEqualTo(exceptionName);
        assertThat(sentDto.getErrorCodeMessage()).isEqualTo(errorCodeMessage);
        assertThat(sentDto.getMessage()).isEqualTo(message);
        assertThat(sentDto.getStackSummary()).isEqualTo(stackSummary);
        assertThat(sentDto.getSource()).isEqualTo(source);
        assertThat(sentDto.getErrorCategory()).isEqualTo(errorCategory);
    }
}