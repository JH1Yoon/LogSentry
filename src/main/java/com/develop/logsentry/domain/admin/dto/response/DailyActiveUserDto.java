package com.develop.logsentry.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "일별 활성 사용자 응답 DTO")
public class DailyActiveUserDto {
    @Schema(description = "날짜 (yyyy-MM-dd)", example = "2025-06-30")
    private String date;

    @Schema(description = "해당 날짜의 활성 사용자 수", example = "42")
    private Long activeUserCount;

    public DailyActiveUserDto(String date, Long activeUserCount) {
        this.date = date;
        this.activeUserCount = activeUserCount;
    }
}
