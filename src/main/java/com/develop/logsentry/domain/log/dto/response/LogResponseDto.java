package com.develop.logsentry.domain.log.dto.response;

import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.entity.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LogResponseDto {
    private Long id;
    private LogLevel logLevel;
    private String exceptionName;
    private String errorCodeMessage;
    private String message;
    private String stackSummary;
    private LocalDateTime timestamp;

    public static LogResponseDto from(Log log) {
        return new LogResponseDto(
                log.getId(),
                log.getLogLevel(),
                log.getExceptionName(),
                log.getErrorCodeMessage(),
                log.getMessage(),
                log.getStackSummary(),
                log.getTimestamp()
        );
    }
}