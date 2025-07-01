package com.develop.logsentry.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDashboardResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 이름", example = "로그 모니터링 시스템")
    private String projectName;

    @Schema(description = "생성일", example = "2025-07-01T14:30:00")
    private String createdAt;

    @Schema(description = "팀 멤버 수", example = "5")
    private int teamMemberCount;

    @Schema(description = "최근 로그 수", example = "148")
    private int recentLogCount;

    @Schema(description = "API 키 사용 횟수", example = "1032")
    private int apiKeyUsageCount;

    public ProjectDashboardResponseDto(Long id, String projectName, String createdAt,
                                       int teamMemberCount, int recentLogCount, int apiKeyUsageCount) {
        this.id = id;
        this.projectName = projectName;
        this.createdAt = createdAt;
        this.teamMemberCount = teamMemberCount;
        this.recentLogCount = recentLogCount;
        this.apiKeyUsageCount = apiKeyUsageCount;
    }
}