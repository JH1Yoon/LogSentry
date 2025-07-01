package com.develop.logsentry.domain.team.dto.response;

import com.develop.logsentry.domain.team.entity.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateMemberResponseDto {
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "변경된 역할", example = "ADMIN")
    private TeamRole role;

    public UpdateMemberResponseDto(String email, String name, TeamRole role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }
}