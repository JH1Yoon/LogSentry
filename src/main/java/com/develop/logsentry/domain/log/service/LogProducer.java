package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import com.develop.logsentry.domain.log.entity.LogLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogProducer {
    private static final String TOPIC = "logs-topic";
    private final KafkaTemplate<String, LogMessageDto> kafkaTemplate;

    public void sendLog(LogLevel logLevel, String exceptionName, String errorCodeMessage, String message,
            String stackSummary, String source, String errorCategory)
    {
        LogMessageDto logMessage = LogMessageDto.of(logLevel, exceptionName, errorCodeMessage, message,
                stackSummary, source, errorCategory);
        kafkaTemplate.send(TOPIC, logMessage);
    }
}