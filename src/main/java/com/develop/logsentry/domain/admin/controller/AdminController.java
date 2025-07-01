package com.develop.logsentry.domain.admin.controller;

import com.develop.logsentry.domain.admin.dto.request.UserRoleUpdateRequestDto;
import com.develop.logsentry.domain.admin.dto.response.*;
import com.develop.logsentry.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "관리자 기능 API")
@RequestMapping("/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/overview")
    @Operation(summary = "시스템 전체 개요 통계 조회", responses = {
            @ApiResponse(responseCode = "200", description = "전체 통계 조회 성공")
    })
    public ResponseEntity<AdminOverviewResponseDto> getOverview() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getOverview());
    }

    @GetMapping("/daily-active-users")
    @Operation(summary = "일별 활성 사용자 수 조회", description = "지정한 기간 내의 일별 사용자 활동 통계를 조회합니다.")
    public ResponseEntity<List<DailyActiveUserDto>> getDailyUsers(
            @Parameter(description = "조회 시작일", example = "2025-07-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "조회 종료일", example = "2025-07-07T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.status(HttpStatus.OK).body(adminService.getDailyActiveUsers(start, end));
    }

    @GetMapping("/monthly-project-activity")
    @Operation(summary = "월별 프로젝트 활동 통계 조회", description = "지정한 기간 내의 월별 프로젝트 활동량을 반환합니다.")
    public ResponseEntity<List<MonthlyProjectActivityDto>> getMonthlyProjectActivity(
            @Parameter(description = "조회 시작일", example = "2025-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "조회 종료일", example = "2025-07-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.status(HttpStatus.OK).body(adminService.getMonthlyProjectActivity(start, end));
    }

    @GetMapping
    @Operation(summary = "사용자 목록 조회", description = "이메일/역할 기반 필터링 및 페이징이 가능한 사용자 목록 조회 API")
    public ResponseEntity<List<UserListResponseDto>> getUserList(
            @Parameter(description = "이메일 필터", example = "admin@example.com") @RequestParam(required = false) String email,
            @Parameter(description = "역할 필터 (USER 또는 ADMIN)", example = "ADMIN") @RequestParam(required = false) String role,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 사이즈", example = "20") @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(adminService.getUserList(email, role, page, size));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다.")
    public ResponseEntity<UserDetailResponseDto> getUserDetail(@Parameter(description = "사용자 ID", example = "3") @PathVariable Long userId) {
        UserDetailResponseDto user = adminService.getUserDetail(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "사용자 역할 변경", description = "특정 사용자의 역할을 USER 또는 ADMIN으로 변경합니다.")
    public ResponseEntity<Void> updateUserRole(@Parameter(description = "사용자 ID", example = "2") @PathVariable Long userId,
                                               @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "새 역할 정보", required = true,
                                                       content = @Content(mediaType = "application/json")) @RequestBody UserRoleUpdateRequestDto request) {
        adminService.updateUserRole(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/deactivate")
    @Operation(summary = "사용자 비활성화", description = "해당 사용자를 비활성화하여 로그인을 차단합니다.")
    public ResponseEntity<Void> deactivateUser(@Parameter(description = "사용자 ID", example = "3") @PathVariable Long userId) {
        adminService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
