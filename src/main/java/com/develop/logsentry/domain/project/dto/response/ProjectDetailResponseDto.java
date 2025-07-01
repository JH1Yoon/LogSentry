package com.develop.logsentry.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDetailResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 이름", example = "에러 트래킹 시스템")
    private String name;

    @Schema(description = "프로젝트 설명", example = "실시간 에러 모니터링 및 알림 기능")
    private String description;

    @Schema(description = "API 키", example = "e0ab3df4-4d77-49e7-879a-3a1234567890")
    private String apiKey;

    public ProjectDetailResponseDto(Long id, String name, String description, String apiKey) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.apiKey = apiKey;
    }
}