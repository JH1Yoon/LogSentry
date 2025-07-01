package com.develop.logsentry.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "월별 프로젝트 활동 통계 응답 DTO")
public class MonthlyProjectActivityDto {
    @Schema(description = "월 (yyyy-MM)", example = "2025-06")
    private String month;

    @Schema(description = "프로젝트 활동 수", example = "120")
    private Long count;

    public MonthlyProjectActivityDto(String month, Long count) {
        this.month = month;
        this.count = count;
    }
}
