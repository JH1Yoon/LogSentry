package com.develop.logsentry.domain.user.dto.response;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SignupResponseDto {
    private String email;
    private String username;
    private List<UserRoleEnum> role;

    public SignupResponseDto(String email, String username, List<UserRoleEnum> role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }
}
