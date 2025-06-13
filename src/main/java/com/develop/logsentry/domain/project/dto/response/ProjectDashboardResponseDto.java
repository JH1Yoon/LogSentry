package com.develop.logsentry.domain.project.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDashboardResponseDto {
    private Long id;
    private String projectName;
    private String createdAt;
    private int teamMemberCount;
    private int recentLogCount;
    private int apiKeyUsageCount;

    public ProjectDashboardResponseDto(Long id, String projectName, String createdAt, int teamMemberCount, int recentLogCount, int apiKeyUsageCount) {
        this.id = id;
        this.projectName = projectName;
        this.createdAt = createdAt;
        this.teamMemberCount = teamMemberCount;
        this.recentLogCount = recentLogCount;
        this.apiKeyUsageCount = apiKeyUsageCount;
    }
}
