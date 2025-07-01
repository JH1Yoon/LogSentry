package com.develop.logsentry.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 역할 변경 요청 DTO")
public class UserRoleUpdateRequestDto  {
    @Schema(description = "새 역할 (USER 또는 ADMIN)", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newRole;

    public UserRoleUpdateRequestDto (String newRole) {
        this.newRole = newRole;
    }
}
