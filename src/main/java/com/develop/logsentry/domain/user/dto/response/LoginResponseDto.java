package com.develop.logsentry.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }
}