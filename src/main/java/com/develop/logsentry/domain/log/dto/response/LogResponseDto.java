package com.develop.logsentry.domain.log.dto.response;

import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.entity.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LogResponseDto {
    @Schema(description = "로그 ID", example = "1001")
    private Long id;

    @Schema(description = "로그 레벨", example = "ERROR")
    private LogLevel logLevel;

    @Schema(description = "예외 이름", example = "IllegalArgumentException")
    private String exceptionName;

    @Schema(description = "에러 코드 메시지", example = "E400_ILLEGAL_ARGUMENT")
    private String errorCodeMessage;

    @Schema(description = "에러 메시지", example = "Invalid input provided")
    private String message;

    @Schema(description = "스택 요약", example = "com.develop.service.UserService.createUser(UserService.java:45)")
    private String stackSummary;

    @Schema(description = "발생 시각", example = "2025-06-30T22:15:00")
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