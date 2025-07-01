package com.develop.logsentry.domain.user.dto.request;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequestDto {
    @Schema(description = "이메일", example = "newuser@example.com")
    private String email;

    @Schema(description = "유저명", example = "NewUser")
    private String username;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    public SignupRequestDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}