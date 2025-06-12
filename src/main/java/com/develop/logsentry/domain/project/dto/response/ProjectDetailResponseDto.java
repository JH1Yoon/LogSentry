package com.develop.logsentry.domain.project.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDetailResponseDto {
    private Long id;
    private String name;
    private String description;
    private String apiKey;

    public ProjectDetailResponseDto(Long id, String name, String description, String apiKey) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.apiKey = apiKey;
    }
}
