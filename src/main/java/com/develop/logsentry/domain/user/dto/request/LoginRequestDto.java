package com.develop.logsentry.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "securePassword")
    private String password;

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}