package com.develop.logsentry.domain.user.dto.response;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SignupResponseDto {
    @Schema(description = "이메일", example = "newuser@example.com")
    private String email;

    @Schema(description = "유저명", example = "NewUser")
    private String username;

    @Schema(description = "권한 목록", example = "[\"USER\"]")
    private List<UserRoleEnum> role;

    public SignupResponseDto(String email, String username, List<UserRoleEnum> role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }
}