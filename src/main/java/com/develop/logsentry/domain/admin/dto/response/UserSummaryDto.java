package com.develop.logsentry.domain.admin.dto.response;

import lombok.Getter;

@Getter
public class UserSummaryDto {
    private Long id;
    private String email;
    private String role;
    private boolean active;
    private String joinedAt;

    public UserSummaryDto(Long id, String email, String role, boolean active, String joinedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.active = active;
        this.joinedAt = joinedAt;
    }
}
