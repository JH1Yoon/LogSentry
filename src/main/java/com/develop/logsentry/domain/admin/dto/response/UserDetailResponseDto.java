package com.develop.logsentry.domain.admin.dto.response;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "사용자 상세 정보 응답 DTO")
public class UserDetailResponseDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "admin@example.com")
    private String email;

    @Schema(description = "유저명", example = "관리자")
    private String username;

    @Schema(description = "권한", example = "ADMIN")
    private UserRoleEnum role;

    @Schema(description = "활성 상태", example = "true")
    private boolean isActive;

    @Schema(description = "계정 생성일", example = "2025-01-01T00:00:00")
    private String createdAt;

    @Schema(description = "삭제일 (없으면 null)", example = "2025-07-01T10:00:00")
    private String deletedAt;

    public UserDetailResponseDto(Long id, String email, String username, UserRoleEnum role,
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