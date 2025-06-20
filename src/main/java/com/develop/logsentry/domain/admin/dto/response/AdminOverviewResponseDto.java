package com.develop.logsentry.domain.admin.dto.response;

import lombok.Getter;

@Getter
public class AdminOverviewResponseDto {
    private Long totalUsers;
    private Long totalProjects;
    private Long totalLogs;

    public AdminOverviewResponseDto(Long totalUsers, Long totalProjects, Long totalLogs) {
        this.totalUsers = totalUsers;
        this.totalProjects = totalProjects;
        this.totalLogs = totalLogs;
    }
}
