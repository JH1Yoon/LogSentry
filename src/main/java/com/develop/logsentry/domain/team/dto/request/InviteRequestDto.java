package com.develop.logsentry.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteRequestDto {
    @Schema(description = "초대할 사용자 이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "이메일 형식을 맞춰야 합니다.")
    private String email;

    public InviteRequestDto(String email) {
        this.email = email;
    }
}