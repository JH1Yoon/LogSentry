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
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.develop.logsentry.domain.user.entity.UserTeam;
import com.develop.logsentry.domain.user.repository.UserRepository;
import com.develop.logsentry.domain.user.repository.UserTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserTeamRepository userTeamRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private ProjectService projectService;

    private User mockUser;
    private Team mockTeam;
    private Project project1, project2;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "tester", "test@example.com", "1234", UserRoleEnum.USER, null, true, null);
        mockTeam = Team.builder().id(1L).name("Test Team").build();
        project1 = Project.builder().id(1L).name("Project 1").description("Desc 1").team(mockTeam).createdAt(LocalDateTime.now()).isActive(true).build();
        project2 = Project.builder().id(2L).name("Project 2").description("Desc 2").team(mockTeam).createdAt(LocalDateTime.now()).isActive(true).build();
    }

    @Test
    @DisplayName("프로젝트 생성 - 성공")
    void createProject_success() {
        // Given
        ProjectRequestDto dto = new ProjectRequestDto("Project 1", "설명");
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(anyString())).thenReturn(mockUser);
        when(teamRepository.findByIdOrThrow(1L)).thenReturn(mockTeam);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, 1L)).thenReturn(new UserTeam());
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return Project.builder()
                    .id(100L)
                    .name(p.getName())
                    .description(p.getDescription())
                    .team(p.getTeam())
                    .createdBy(p.getCreatedBy())
                    .apiKey(p.getApiKey())
                    .isActive(p.isActive())
                    .build();
        });

        // When
        ProjectResponseDto result = projectService.createProject(1L, mockUser, dto);

        // Then
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("Project 1");
        assertThat(result.getDescription()).isEqualTo("설명");
    }

    @Test
    @DisplayName("팀 내 프로젝트 목록 조회 - 성공")
    void getProjectsByTeam_success() {
        // Given
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(anyString())).thenReturn(mockUser);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, 1L)).thenReturn(new UserTeam());
        when(projectRepository.findByTeamId(1L)).thenReturn(List.of(project1, project2));

        // When
        List<ProjectResponseDto> result = projectService.getProjectsByTeam(1L, mockUser);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Project 1");
        verify(projectRepository).findByTeamId(1L);
    }

    @Test
    @DisplayName("프로젝트 상세 조회 - 성공")
    void getProjectDetail_success() {
        // Given
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(anyString())).thenReturn(mockUser);
        when(projectRepository.findByIdAndIsActiveTrueOrThrow(1L)).thenReturn(project1);
        when(projectRepository.findByIdOrThrow(1L)).thenReturn(project1);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(mockTeam.getId(), mockUser.getId())).thenReturn(new UserTeam());

        // When
        ProjectDetailResponseDto detail = projectService.getProjectDetail(1L, mockUser);

        // Then
        assertThat(detail.getId()).isEqualTo(1L);
        assertThat(detail.getName()).isEqualTo("Project 1");
        assertThat(detail.getDescription()).isEqualTo("Desc 1");
    }

    @Test
    @DisplayName("프로젝트 정보 수정 - 성공")
    void updateProject_success() {
        // Given
        ProjectUpdateRequestDto updateDto = new ProjectUpdateRequestDto("Updated Project", "Updated Desc");

        when(userRepository.findByEmailAndIsActiveTrueOrThrow(anyString())).thenReturn(mockUser);
        when(projectRepository.findByIdAndIsActiveTrueOrThrow(1L)).thenReturn(project1);
        when(projectRepository.findByIdOrThrow(1L)).thenReturn(project1);
        doNothing().when(projectRepository).existsByIdAndCreatedByIdOrThrow(1L, mockUser.getId());

        // When
        ProjectResponseDto response = projectService.updateProject(1L, updateDto, mockUser);

        // Then
        assertThat(response.getName()).isEqualTo("Updated Project");
        assertThat(response.getDescription()).isEqualTo("Updated Desc");
    }

    @Test
    @DisplayName("프로젝트 삭제 - 성공")
    void deleteProject_success() {
        // Given
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(anyString())).thenReturn(mockUser);
        when(projectRepository.findByIdAndIsActiveTrueOrThrow(1L)).thenReturn(project1);
        when(projectRepository.findByIdOrThrow(1L)).thenReturn(project1);
        doNothing().when(projectRepository).existsByIdAndCreatedByIdOrThrow(1L, mockUser.getId());

        // When
        projectService.deleteProject(1L, mockUser);

        // Then
        assertThat(project1.isActive()).isFalse();
        assertThat(project1.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("프로젝트 대시보드 조회 - 성공")
    void getProjectDashboard_success() {
        // Given
        when(projectRepository.findByIdAndIsActiveTrueOrThrow(1L)).thenReturn(project1);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(mockTeam.getId(), mockUser.getId())).thenReturn(new UserTeam());
        when(userTeamRepository.countByTeamId(mockTeam.getId())).thenReturn(5);
        when(logRepository.countByProjectIdLegacyAndTimestampAfter(eq(1L), any())).thenReturn(10L);

        // When
        ProjectDashboardResponseDto dashboard = projectService.getProjectDashboard(mockUser, 1L);

        // Then
        assertThat(dashboard.getId()).isEqualTo(1L);
        assertThat(dashboard.getProjectName()).isEqualTo("Project 1");
        assertThat(dashboard.getTeamMemberCount()).isEqualTo(5);
        assertThat(dashboard.getRecentLogCount()).isEqualTo(10);
        assertThat(dashboard.getApiKeyUsageCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("API 키 재발급 - 성공")
    void regenerateApiKey_success() {
        // Given
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(anyString())).thenReturn(mockUser);
        when(projectRepository.findByIdAndIsActiveTrueOrThrow(1L)).thenReturn(project1);
        doNothing().when(projectRepository).existsByIdAndCreatedByIdOrThrow(1L, mockUser.getId());
        when(projectRepository.findByIdOrThrow(1L)).thenReturn(project1);

        String oldApiKey = project1.getApiKey();

        // When
        ApiKeyResponseDto response = projectService.regenerateApiKey(1L, mockUser);

        // Then
        assertThat(response.getApiKey()).isNotNull();
        assertThat(response.getApiKey()).isNotEqualTo(oldApiKey);
    }
}