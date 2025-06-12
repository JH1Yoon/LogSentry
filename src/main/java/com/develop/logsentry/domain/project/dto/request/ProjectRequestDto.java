package com.develop.logsentry.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectRequestDto {
    @NotBlank(message = "프로젝트 이름을 입력해야합니다.")
    private String name;

    private String description;
}
