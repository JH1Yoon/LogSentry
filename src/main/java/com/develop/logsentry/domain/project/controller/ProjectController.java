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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Project", description = "프로젝트 관련 API")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "지정한 팀에 새로운 프로젝트를 생성합니다.")
    @PostMapping("/team/{teamId}")
    public ResponseEntity<ProjectResponseDto> createProject(
            @Parameter(description = "팀 ID") @PathVariable Long teamId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ProjectRequestDto projectRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(teamId, userDetails.getUser(), projectRequestDto));
    }

    @Operation(summary = "팀 내 프로젝트 목록 조회", description = "팀에 속한 모든 프로젝트 목록을 조회합니다.")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ProjectResponseDto>> getProjectsByTeam(
            @Parameter(description = "팀 ID") @PathVariable Long teamId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.getProjectsByTeam(teamId, userDetails.getUser()));
    }

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트의 상세 정보를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponseDto> getProjectById(
            @Parameter(description = "프로젝트 ID") @PathVariable Long projectId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.getProjectDetail(projectId, userDetails.getUser()));
    }

    @Operation(summary = "프로젝트 수정", description = "프로젝트 정보를 수정합니다.")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @Parameter(description = "프로젝트 ID") @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequestDto dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.updateProject(projectId, dto, userDetails.getUser()));
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<SuccessResponse> deleteProject(
            @Parameter(description = "프로젝트 ID") @PathVariable Long projectId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        projectService.deleteProject(projectId, userDetails.getUser());
        return ResponseEntity.ok(new SuccessResponse(200, "프로젝트가 성공적으로 삭제되었습니다."));
    }

    @Operation(summary = "프로젝트 대시보드 조회", description = "프로젝트 통계 및 활동 대시보드를 조회합니다.")
    @GetMapping("/{projectId}/dashboard")
    public ResponseEntity<ProjectDashboardResponseDto> getDashboard(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "프로젝트 ID") @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getProjectDashboard(userDetails.getUser(), projectId));
    }

    @Operation(summary = "API 키 재발급", description = "해당 프로젝트의 API 키를 재발급합니다.")
    @PostMapping("/{projectId}/apikey/regenerate")
    public ResponseEntity<ApiKeyResponseDto> regenerateApiKey(
            @Parameter(description = "프로젝트 ID") @PathVariable Long projectId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.regenerateApiKey(projectId, userDetails.getUser()));
    }
}