package com.develop.logsentry.domain.log.controller;

import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.dto.response.LogResponseDto;
import com.develop.logsentry.domain.log.dto.response.LogStatisticsDto;
import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.service.LogService;
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

    // 로그 조건 검색
    @GetMapping
    public ResponseEntity<Page<LogResponseDto>> getLogs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) LogLevel logLevel,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(logService.getLogs(userDetails.getUser(), projectId, logLevel, start, end, page, size));
    }

    // 로그 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<LogResponseDto> getLogDetail(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(logService.getLogDetail(userDetails.getUser(), id));
    }

    // 로그 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<List<LogStatisticsDto>> getStatistics(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.status(HttpStatus.OK).body(logService.getLogStatistics(userDetails.getUser(), projectId, start, end));
    }
}
