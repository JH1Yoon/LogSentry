package com.develop.logsentry.domain.admin.dto.response;

import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "사용자 목록 조회 응답 DTO")
public class UserListResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "유저명", example = "홍길동")
    private String username;

    @Schema(description = "권한", example = "USER")
    private UserRoleEnum role;

    @Schema(description = "활성 상태", example = "true")
    private boolean isActive;

    public UserListResponseDto(Long id, String email, String username, UserRoleEnum role, boolean isActive) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
        this.isActive = isActive;
    }
}