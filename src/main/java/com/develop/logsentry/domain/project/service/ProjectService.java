package com.develop.logsentry.domain.project.service;

import com.develop.logsentry.domain.log.repository.LogRepository;
import com.develop.logsentry.domain.project.dto.request.ProjectRequestDto;
import com.develop.logsentry.domain.project.dto.request.ProjectUpdateRequestDto;
import com.develop.logsentry.domain.project.dto.response.ApiKeyResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectDashboardResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectDetailResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectResponseDto;
import com.develop.logsentry.domain.project.entity.Project;
import com.develop.logsentry.domain.project.repository.ProjectRepository;
import com.develop.logsentry.domain.team.entity.Team;
import com.develop.logsentry.domain.team.repository.TeamRepository;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserTeam;
import com.develop.logsentry.domain.user.repository.UserRepository;
import com.develop.logsentry.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final UserTeamRepository userTeamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final LogRepository logRepository;

    /**
     * 프로젝트 생성
     *
     * @param teamId
     * @param user
     * @param projectRequestDto
     * @return ProjectResponseDto
     */
    @Transactional
    public ProjectResponseDto createProject(Long teamId, User user, ProjectRequestDto projectRequestDto) {
        userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());

        Team team = teamRepository.findByIdOrThrow(teamId);
        UserTeam userTeam = userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, user.getId());

        Project project = Project.builder()
                .team(team)
                .name(projectRequestDto.getName())
                .description(projectRequestDto.getDescription())
                .apiKey(UUID.randomUUID().toString())
                .createdBy(user)
                .isActive(true)
                .build();

        projectRepository.save(project);

        return new ProjectResponseDto(project);

    }

    /**
     * 팀 내 프로젝트 목록 조회
     *
     * @param teamId
     * @param user
     * @return List<ProjectResponseDto>
     */
    public List<ProjectResponseDto> getProjectsByTeam(Long teamId, User user) {
        userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());
        userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, user.getId());

        return projectRepository.findByTeamId(teamId).stream()
                .filter(p -> p.isActive())
                .map(ProjectResponseDto::new)
                .toList();
    }

    /**
     * 프로젝트 상세 조회
     *
     * @param projectId
     * @param user
     * @return ProjectDetailResponseDto
     */
    public ProjectDetailResponseDto getProjectDetail(Long projectId, User user) {
        userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());
        projectRepository.findByIdAndIsActiveTrueOrThrow(projectId);

        Project project = projectRepository.findByIdOrThrow(projectId);
        Long teamId = project.getTeam().getId();

        userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, user.getId());

        return new ProjectDetailResponseDto(project.getId(), project.getName(), project.getDescription(), project.getApiKey());
    }

    /**
     * 프로젝트 정보 수정
     *
     * @param projectId
     * @param request
     * @param user
     * @return ProjectResponseDto
     */
    @Transactional
    public ProjectResponseDto updateProject(Long projectId, ProjectUpdateRequestDto request, User user) {
        userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());
        projectRepository.findByIdAndIsActiveTrueOrThrow(projectId);

        Project project = projectRepository.findByIdOrThrow(projectId);

        projectRepository.existsByIdAndCreatedByIdOrThrow(projectId, user.getId());

        project.update(request.getName(), request.getDescription());

        return new ProjectResponseDto(project);
    }

    /**
     * 프로젝트 삭제
     *
     * @param projectId
     */
    @Transactional
    public void deleteProject(Long projectId, User user) {
        userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());
        projectRepository.findByIdAndIsActiveTrueOrThrow(projectId);

        Project project = projectRepository.findByIdOrThrow(projectId);
        projectRepository.existsByIdAndCreatedByIdOrThrow(projectId, user.getId());

        project.deactivate();
    }

    /**
     * 프로젝트 통계 및 대시보드
     *
     * @param user
     * @param projectId
     * @return ProjectDashboardResponseDto
     */
    public ProjectDashboardResponseDto getProjectDashboard(User user, Long projectId) {
        Project project = projectRepository.findByIdAndIsActiveTrueOrThrow(projectId);

        Long teamId = project.getTeam().getId();
        userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, user.getId());

        int teamMemberCount = userTeamRepository.countByTeamId(teamId);
        int recentLogCount = (int) logRepository.countByProjectIdLegacyAndTimestampAfter(projectId, LocalDateTime.now().minusDays(7));
        int apiKeyUsageCount = 0; // apiKeyUsageRepository.countByProjectId(...)

        return new ProjectDashboardResponseDto(
                project.getId(),
                project.getName(),
                project.getCreatedAt().toString(),
                teamMemberCount,
                recentLogCount,
                apiKeyUsageCount
        );
    }

    /**
     * API 키 재발급
     *
     * @param projectId
     * @return ApiKeyResponseDto
     */
    @Transactional
    public ApiKeyResponseDto regenerateApiKey(Long projectId, User user) {
        userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());
        projectRepository.findByIdAndIsActiveTrueOrThrow(projectId);
        projectRepository.existsByIdAndCreatedByIdOrThrow(projectId, user.getId());

        Project project = projectRepository.findByIdOrThrow(projectId);
        String newKey = project.regenerateApiKey();
        return new ApiKeyResponseDto(newKey);
    }
}
