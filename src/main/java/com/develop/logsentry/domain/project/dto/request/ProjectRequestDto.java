package com.develop.logsentry.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectRequestDto {
    @Schema(description = "프로젝트 이름", example = "에러 추적 시스템")
    @NotBlank(message = "프로젝트 이름을 입력해야합니다.")
    private String name;

    @Schema(description = "프로젝트 설명", example = "로그 수집 및 에러 알림 프로젝트")
    private String description;

    public ProjectRequestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}