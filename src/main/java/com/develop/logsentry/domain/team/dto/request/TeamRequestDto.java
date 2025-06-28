package com.develop.logsentry.domain.team.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TeamRequestDto {
    @NotBlank(message = "팀 이름을 입력해야 합니다.")
    private String name;

    private String description;

    public TeamRequestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
