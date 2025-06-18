package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final LogRepository logRepository;

    @KafkaListener(topics = "logs-topic", groupId = "log_group")
    public void consume(LogMessageDto dto) {
        try {
            Log log = Log.builder()
                    .logLevel(dto.getLogLevel())
                    .exceptionName(dto.getExceptionName())
                    .errorCodeMessage(dto.getErrorCodeMessage())
                    .message(dto.getMessage())
                    .stackSummary(dto.getStackSummary())
                    .timestamp(dto.getTimestamp())
                    .build();

            logRepository.save(log);

        } catch (Exception e) {
            System.err.println("Kafka 메시지 처리 실패: " + e.getMessage());
            log.error("Kafka 메시지 처리 실패", e);
        }
    }
}