package com.develop.logsentry.domain.project.dto.response;

import com.develop.logsentry.domain.project.entity.Project;
import lombok.Getter;

@Getter
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;

    public ProjectResponseDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
    }
}
