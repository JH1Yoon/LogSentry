package com.develop.logsentry.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectUpdateRequestDto {
    @Schema(description = "프로젝트 이름 (선택)", example = "New Project Name", nullable = true)
    private String name;

    @Schema(description = "프로젝트 설명 (선택)", example = "Updated project description", nullable = true)
    private String description;
}
