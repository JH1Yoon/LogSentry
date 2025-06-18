package com.develop.logsentry.domain.log.dto.request;

import com.develop.logsentry.domain.log.entity.LogLevel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LogSearchCondition {
    private LogLevel logLevel;
    private String keyword;
    private LocalDateTime from;
    private LocalDateTime to;
}