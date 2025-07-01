package com.develop.logsentry.domain.team.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TeamResponseDto {
    @Schema(description = "팀 ID", example = "1")
    private Long teamId;

    @Schema(description = "팀 이름", example = "Dev Team")
    private String name;

    @Schema(description = "팀 설명", example = "백엔드 개발팀")
    private String description;

    @Schema(description = "생성일시", example = "2025-07-01T15:00:00")
    private LocalDateTime createdAt;

    public TeamResponseDto(Long teamId, String name, String description, LocalDateTime createdAt) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
}