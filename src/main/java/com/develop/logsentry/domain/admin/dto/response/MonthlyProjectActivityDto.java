package com.develop.logsentry.domain.admin.dto.response;

import lombok.Getter;

@Getter
public class MonthlyProjectActivityDto {
    private String month;
    private Long count;

    public MonthlyProjectActivityDto(String month, Long count) {
        this.month = month;
        this.count = count;
    }
}
