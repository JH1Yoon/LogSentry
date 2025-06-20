package com.develop.logsentry.domain.admin.dto.response;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserDetailResponseDto  {
    private Long id;
    private String email;
    private String username;
    private UserRoleEnum role;
    private boolean isActive;
    private String createdAt;
    private String deletedAt;

    public UserDetailResponseDto (Long id, String email, String username, UserRoleEnum role,
                                  boolean isActive, String createdAt, String deletedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }
}
