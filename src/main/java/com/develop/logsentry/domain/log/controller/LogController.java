package com.develop.logsentry.domain.log.controller;

import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.dto.response.LogResponseDto;
import com.develop.logsentry.domain.log.dto.response.LogStatisticsDto;
import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/log")
public class LogController {

    private final LogService logService;

    @Operation(summary = "로그 검색", description = "조건에 따라 로그를 검색합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그 목록 반환"),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
            })
    @GetMapping
    public ResponseEntity<Page<LogResponseDto>> getLogs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "프로젝트 ID", example = "1") @RequestParam(required = false) Long projectId,
            @Parameter(description = "로그 레벨", example = "ERROR") @RequestParam(required = false) LogLevel logLevel,
            @Parameter(description = "조회 시작 시각", example = "2025-06-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "조회 종료 시각", example = "2025-06-30T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(
                logService.getLogs(userDetails.getUser(), projectId, logLevel, start, end, page, size));
    }

    @Operation(summary = "로그 상세 조회", description = "로그 ID로 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그 상세 정보 반환"),
                    @ApiResponse(responseCode = "404", description = "해당 로그 없음", content = @Content)
            })
    @GetMapping("/{id}")
    public ResponseEntity<LogResponseDto> getLogDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "로그 ID", example = "123") @PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(
                logService.getLogDetail(userDetails.getUser(), id));
    }

    @Operation(summary = "로그 통계 조회", description = "기간 및 프로젝트 조건에 따라 로그 통계를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그 통계 반환"),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
            })
    @GetMapping("/statistics")
    public ResponseEntity<List<LogStatisticsDto>> getStatistics(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "프로젝트 ID", example = "1") @RequestParam(required = false) Long projectId,
            @Parameter(description = "시작 시각", example = "2025-06-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "종료 시각", example = "2025-06-30T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.status(HttpStatus.OK).body(
                logService.getLogStatistics(userDetails.getUser(), projectId, start, end));
    }
}