package com.develop.logsentry.domain.team.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TeamResponseDto {
    private Long teamId;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public TeamResponseDto(Long teamId, String name, String description, LocalDateTime createdAt) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
}
