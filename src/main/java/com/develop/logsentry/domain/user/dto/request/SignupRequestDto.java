package com.develop.logsentry.domain.user.dto.request;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequestDto {
    private String email;
    private String username;
    private String password;

    public SignupRequestDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
