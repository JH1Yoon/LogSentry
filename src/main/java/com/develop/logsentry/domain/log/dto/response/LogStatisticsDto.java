package com.develop.logsentry.domain.log.dto.response;

import com.develop.logsentry.domain.log.entity.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogStatisticsDto {

    @Schema(description = "로그 레벨", example = "ERROR")
    private LogLevel logLevel;

    @Schema(description = "로그 수", example = "1234")
    private long count;
}