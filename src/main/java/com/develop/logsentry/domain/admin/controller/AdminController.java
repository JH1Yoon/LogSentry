package com.develop.logsentry.domain.admin.controller;

import com.develop.logsentry.domain.admin.dto.request.UserRoleUpdateRequestDto;
import com.develop.logsentry.domain.admin.dto.response.*;
import com.develop.logsentry.domain.admin.service.AdminService;
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
@RequestMapping("/v1/admin")
public class AdminController {
    private final AdminService adminService;

    // 관리자용 전체 통계 조회
    @GetMapping("/overview")
    public ResponseEntity<AdminOverviewResponseDto> getOverview() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getOverview());
    }

    // 	기간 내 일별 활성 사용자 수 조회
    @GetMapping("/daily-active-users")
    public ResponseEntity<List<DailyActiveUserDto>> getDailyUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.status(HttpStatus.OK).body(adminService.getDailyActiveUsers(start, end));
    }

    // 기간 내 월별 프로젝트 활동 통계 조회
    @GetMapping("/monthly-project-activity")
    public ResponseEntity<List<MonthlyProjectActivityDto>> getMonthlyProjectActivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.status(HttpStatus.OK).body(adminService.getMonthlyProjectActivity(start, end));
    }

    // 	사용자 목록 조회 및 필터링
    @GetMapping
    public ResponseEntity<List<UserListResponseDto>> getUserList(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(adminService.getUserList(email, role, page, size));
    }

    // 사용자 상세 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponseDto> getUserDetail(@PathVariable Long userId) {
        UserDetailResponseDto user = adminService.getUserDetail(userId);
        return ResponseEntity.ok(user);
    }

    // 	사용자 역할 변경
    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId, @RequestBody UserRoleUpdateRequestDto request) {
        adminService.updateUserRole(userId, request);
        return ResponseEntity.noContent().build();
    }

    // 사용자 비활성화(로그인 차단)
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        adminService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
