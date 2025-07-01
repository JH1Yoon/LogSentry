package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.repository.LogRepository;
import com.develop.logsentry.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogConsumer {

//    private final AlertService alertService;
    private final LogRepository logRepository;
    private final ProjectRepository projectRepository;

    @KafkaListener(topics = "logs-topic", groupId = "log_group")
    public void consume(LogMessageDto dto) {
        try {
            Long projectId = null;
            if (dto.getProjectId() != null) {
                boolean exists = projectRepository.existsById(dto.getProjectId());
                log.info("dto.getProjectId() = {}", dto.getProjectId());
                if (exists) {
                    projectId = dto.getProjectId();
                    log.info("Kafka Log 저장 시 Project ID {} -> 존재", dto.getProjectId());
                } else {
                    log.info("Kafka Log 저장 시 Project ID {} -> NULL", dto.getProjectId());
                }
            }

            Log log = Log.builder()
                    .logLevel(dto.getLogLevel())
                    .exceptionName(dto.getExceptionName())
                    .errorCodeMessage(dto.getErrorCodeMessage())
                    .message(dto.getMessage())
                    .stackSummary(dto.getStackSummary())
                    .timestamp(dto.getTimestamp())
                    .projectIdLegacy(projectId)
                    .build();

            logRepository.save(log);

//            if (dto.getLogLevel().equals(LogLevel.ERROR)) {
//                alertService.sendLogErrorAlert(dto);
//            }

        } catch (Exception e) {
            System.err.println("Kafka 메시지 처리 실패: " + e.getMessage());
            log.error("Kafka 메시지 처리 실패", e);
        }
    }
}