package com.develop.logsentry.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteRequestDto {
    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    public DeleteRequestDto(String password) {
        this.password = password;
    }
}