package com.develop.logsentry.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "관리자 전체 통계 응답 DTO")
public class AdminOverviewResponseDto {
    @Schema(description = "총 사용자 수", example = "3")
    private Long totalUsers;

    @Schema(description = "총 프로젝트 수", example = "2")
    private Long totalProjects;

    @Schema(description = "총 로그 수", example = "0")
    private Long totalLogs;

    public AdminOverviewResponseDto(Long totalUsers, Long totalProjects, Long totalLogs) {
        this.totalUsers = totalUsers;
        this.totalProjects = totalProjects;
        this.totalLogs = totalLogs;
    }
}
