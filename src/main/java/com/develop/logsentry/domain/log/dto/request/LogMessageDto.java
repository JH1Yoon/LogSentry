package com.develop.logsentry.domain.log.dto.request;

import com.develop.logsentry.domain.log.entity.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LogMessageDto {
    private final LogLevel logLevel;
    private final String exceptionName;
    private final String errorCodeMessage;
    private final String message;
    private final String stackSummary;
    private final String source;
    private final LocalDateTime timestamp;
    private final String errorCategory;

    public static LogMessageDto of(LogLevel logLevel, String exceptionName, String errorCodeMessage,
                                   String message, String stackSummary, String source, String errorCategory) {
        return LogMessageDto.builder()
                .logLevel(logLevel)
                .exceptionName(exceptionName)
                .errorCodeMessage(errorCodeMessage)
                .message(message)
                .stackSummary(stackSummary)
                .source(source)
                .timestamp(LocalDateTime.now())
                .errorCategory(errorCategory.toUpperCase())
                .build();
    }
}