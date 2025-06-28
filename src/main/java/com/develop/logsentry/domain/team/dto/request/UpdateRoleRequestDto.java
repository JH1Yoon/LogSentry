package com.develop.logsentry.domain.team.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRoleRequestDto {
    private String role;

    public UpdateRoleRequestDto(String role) {
        this.role = role;
    }
}
