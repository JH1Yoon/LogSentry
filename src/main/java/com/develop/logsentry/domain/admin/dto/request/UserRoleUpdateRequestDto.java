package com.develop.logsentry.domain.admin.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRoleUpdateRequestDto  {
    private String newRole;

    public UserRoleUpdateRequestDto (String newRole) {
        this.newRole = newRole;
    }
}
