package com.develop.logsentry.domain.team.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteRequestDto {
    @Email(message = "이메일 형식을 맞춰야 합니다.")
    private String email;

    public InviteRequestDto(String email) {
        this.email = email;
    }
}
