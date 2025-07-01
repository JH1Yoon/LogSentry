package com.develop.logsentry.domain.log.dto.request;

import com.develop.logsentry.domain.log.entity.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LogSearchCondition {
    @Schema(description = "로그 레벨", example = "INFO")
    private LogLevel logLevel;

    @Schema(description = "검색 키워드", example = "NullPointerException")
    private String keyword;

    @Schema(description = "조회 시작 시각", example = "2025-06-01T00:00:00")
    private LocalDateTime from;

    @Schema(description = "조회 종료 시각", example = "2025-06-30T23:59:59")
    private LocalDateTime to;
}