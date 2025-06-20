package com.develop.logsentry.domain.admin.dto.response;


import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserListResponseDto {
    private Long id;
    private String email;
    private String username;
    private UserRoleEnum role;
    private boolean isActive;

    public UserListResponseDto(Long id, String email, String username, UserRoleEnum role, boolean isActive) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
        this.isActive = isActive;
    }
}