package com.develop.logsentry.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TeamRequestDto {
    @Schema(description = "팀 이름", example = "개발팀", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "팀 이름을 입력해야 합니다.")
    private String name;

    @Schema(description = "팀 설명", example = "서비스 개발팀", nullable = true)
    private String description;

    public TeamRequestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}