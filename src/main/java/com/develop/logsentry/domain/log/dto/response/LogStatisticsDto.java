package com.develop.logsentry.domain.log.dto.response;

import com.develop.logsentry.domain.log.entity.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogStatisticsDto {
    private LogLevel logLevel;
    private long count;
}