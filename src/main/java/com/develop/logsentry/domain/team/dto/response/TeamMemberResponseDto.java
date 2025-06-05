package com.develop.logsentry.domain.team.dto.response;

import com.develop.logsentry.domain.team.entity.TeamRole;
import com.develop.logsentry.domain.user.entity.User;
import lombok.Getter;

@Getter
public class TeamMemberResponseDto {
    String email;
    String username;
    private TeamRole role;

    public TeamMemberResponseDto(String email, String username, TeamRole role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public TeamMemberResponseDto(User user, TeamRole role) {
        this(user.getEmail(), user.getUsername(), role);
    }
}
