package com.develop.logsentry.domain.team.dto.response;

import com.develop.logsentry.domain.team.entity.TeamRole;
import lombok.Getter;

@Getter
public class UpdateMemberResponseDto {
    private String email;
    private String name;
    private TeamRole role;

    public UpdateMemberResponseDto(String email, String name, TeamRole role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
