package com.develop.logsentry.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "간단한 사용자 요약 정보 DTO")
public class UserSummaryDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "summary@example.com")
    private String email;

    @Schema(description = "권한", example = "USER")
    private String role;

    @Schema(description = "활성 상태", example = "true")
    private boolean active;

    @Schema(description = "가입 일자", example = "2025-01-01T12:00:00")
    private String joinedAt;

    public UserSummaryDto(Long id, String email, String role, boolean active, String joinedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.active = active;
        this.joinedAt = joinedAt;
    }
}