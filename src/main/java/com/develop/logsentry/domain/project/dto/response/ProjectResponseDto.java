package com.develop.logsentry.domain.project.dto.response;

import com.develop.logsentry.domain.project.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProjectResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 이름", example = "로그 수집 시스템")
    private String name;

    @Schema(description = "프로젝트 설명", example = "에러 모니터링용 프로젝트")
    private String description;

    public ProjectResponseDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
    }
}