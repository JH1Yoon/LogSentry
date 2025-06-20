package com.develop.logsentry.domain.admin.dto.response;

import lombok.Getter;

@Getter
public class DailyActiveUserDto {
    private String date;
    private Long activeUserCount;

    public DailyActiveUserDto(String date, Long activeUserCount) {
        this.date = date;
        this.activeUserCount = activeUserCount;
    }
}
