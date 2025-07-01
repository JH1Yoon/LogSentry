package com.develop.logsentry.domain.log.dto.request;

import com.develop.logsentry.domain.log.entity.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LogMessageDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private final Long projectId;

    @Schema(description = "로그 레벨", example = "ERROR")
    private final LogLevel logLevel;

    @Schema(description = "예외 이름", example = "NullPointerException")
    private final String exceptionName;

    @Schema(description = "에러 코드 메시지", example = "E500_NULL_POINTER")
    private final String errorCodeMessage;

    @Schema(description = "에러 메시지", example = "Null reference occurred")
    private final String message;

    @Schema(description = "스택 요약", example = "com.example.MyClass.method(MyClass.java:25)")
    private final String stackSummary;

    @Schema(description = "로그 발생 위치 (클래스/메서드)", example = "com.develop.MyService.process()")
    private final String source;

    @Schema(description = "로그 발생 시각", example = "2025-07-01T12:00:00")
    private final LocalDateTime timestamp;

    @Schema(description = "에러 카테고리", example = "SYSTEM")
    private final String errorCategory;

    public static LogMessageDto of(Long projectId, LogLevel logLevel, String exceptionName, String errorCodeMessage,
                                   String message, String stackSummary, String source, String errorCategory) {
        return LogMessageDto.builder()
                .projectId(projectId)
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