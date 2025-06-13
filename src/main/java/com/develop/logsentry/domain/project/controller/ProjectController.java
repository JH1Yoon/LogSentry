package com.develop.logsentry.domain.project.controller;

import com.develop.logsentry.common.message.SuccessResponse;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.project.dto.request.ProjectRequestDto;
import com.develop.logsentry.domain.project.dto.request.ProjectUpdateRequestDto;
import com.develop.logsentry.domain.project.dto.response.ApiKeyResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectDashboardResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectDetailResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectResponseDto;
import com.develop.logsentry.domain.project.service.ProjectService;
import com.develop.logsentry.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/project")
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    // 프로젝트 생성
    @PostMapping("/team/{teamId}")
    public ResponseEntity<ProjectResponseDto> createProject(@PathVariable Long teamId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid ProjectRequestDto projectRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(teamId, userDetails.getUser(), projectRequestDto));

    }

    // 팀 내 프로젝트 목록 조회
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ProjectResponseDto>> getProjectsByTeam(@PathVariable Long teamId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectsByTeam(teamId, userDetails.getUser()));
    }

    // 프로젝트 상세 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponseDto> getProjectById(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectDetail(projectId, userDetails.getUser()));
    }

    // 프로젝트 정보 수정
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(projectId, dto, userDetails.getUser()));
    }

    // 프로젝트 삭제
    @DeleteMapping("/{projectId}")
    public ResponseEntity<SuccessResponse> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        projectService.deleteProject(projectId, userDetails.getUser());
        return ResponseEntity.ok(new SuccessResponse(200, "프로젝트가 성공적으로 삭제되었습니다."));
    }

    // 프로젝트 통계 및 대시보드
    @GetMapping("/{projectId}/dashboard")
    public ResponseEntity<ProjectDashboardResponseDto> getDashboard(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                    @PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectDashboard(userDetails.getUser(), projectId));
    }

    // API 키 재발급
    @PostMapping("/{projectId}/apikey/regenerate")
    public ResponseEntity<ApiKeyResponseDto> regenerateApiKey(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.regenerateApiKey(projectId, userDetails.getUser()));
    }

}
