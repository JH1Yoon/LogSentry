package com.develop.logsentry.domain.team.dto.response;

import com.develop.logsentry.domain.team.entity.TeamRole;
import com.develop.logsentry.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TeamMemberResponseDto {
    @Schema(description = "사용자 이메일", example = "member@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "팀 내 역할", example = "MEMBER")
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