package com.develop.logsentry.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRoleRequestDto {
    @Schema(description = "변경할 역할", example = "ADMIN 또는 MEMBER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;

    public UpdateRoleRequestDto(String role) {
        this.role = role;
    }
}