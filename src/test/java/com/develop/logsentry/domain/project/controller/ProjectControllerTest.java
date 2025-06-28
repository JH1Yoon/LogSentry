package com.develop.logsentry.domain.project.controller;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.service.LogProducer;
import com.develop.logsentry.domain.project.dto.request.ProjectRequestDto;
import com.develop.logsentry.domain.project.dto.request.ProjectUpdateRequestDto;
import com.develop.logsentry.domain.project.dto.response.ApiKeyResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectDashboardResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectDetailResponseDto;
import com.develop.logsentry.domain.project.dto.response.ProjectResponseDto;
import com.develop.logsentry.domain.project.entity.Project;
import com.develop.logsentry.domain.project.service.ProjectService;
import com.develop.logsentry.domain.team.entity.Team;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaRepository jpaRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private LogProducer logProducer;

    @MockBean
    private AuthFilter authFilter;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private Team team;
    private Project project;
    private Project updateProject;
    private Project projectA;
    private Project projectB;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "tester", "test@example.com", "123456", UserRoleEnum.USER, null, true, null);
        team = Team.builder()
                .id(1L)
                .name("Backend Team")
                .description("Responsible for backend systems")
                .build();

        project = Project.builder()
                .id(1L)
                .name("New Project")
                .description("설명")
                .apiKey(UUID.randomUUID().toString())
                .team(team)
                .build();
        ReflectionTestUtils.setField(project, "id", 1L);

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @Test
    @DisplayName("프로젝트 생성 성공")
    void createProject_success() throws Exception {
        // Given
        ProjectRequestDto requestDto = new ProjectRequestDto("New Project", "설명");
        ProjectResponseDto responseDto = new ProjectResponseDto(project);

        when(projectService.createProject(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/v1/project/team/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    @DisplayName("팀 내 프로젝트 목록 조회 성공")
    void getProjectsByTeam_success() throws Exception {
        // Given
        projectA = Project.builder().id(1L).name("Project A").description("설명").build();
        projectB = Project.builder().id(2L).name("Project B").description("설명").build();
        List<ProjectResponseDto> projectList = List.of(
                new ProjectResponseDto(projectA),
                new ProjectResponseDto(projectB)
        );

        when(projectService.getProjectsByTeam(Mockito.eq(1L), Mockito.any()))
                .thenReturn(projectList);

        // When & Then
        mockMvc.perform(get("/v1/project/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("프로젝트 상세 조회 성공")
    void getProjectDetail_success() throws Exception {
        // Given
        ProjectDetailResponseDto detailDto =
                new ProjectDetailResponseDto(1L, "Project A", "설명", "APIKEY");

        when(projectService.getProjectDetail(Mockito.eq(1L), Mockito.any()))
                .thenReturn(detailDto);

        // When & Then
        mockMvc.perform(get("/v1/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project A"));
    }

    @Test
    @DisplayName("프로젝트 수정 성공")
    void updateProject_success() throws Exception {
        // Given
        ProjectUpdateRequestDto updateDto = new ProjectUpdateRequestDto("Update Project", "업데이트된 설명");
        updateProject = Project.builder()
                .id(1L)
                .name("Update Project")
                .description("업데이트된 설명")
                .apiKey(UUID.randomUUID().toString())
                .team(team)
                .build();
        ProjectResponseDto result = new ProjectResponseDto(updateProject);

        when(projectService.updateProject(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(result);

        // When & Then
        mockMvc.perform(put("/v1/project/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Update Project"));
    }

    @Test
    @DisplayName("프로젝트 삭제 성공")
    void deleteProject_success() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/v1/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("프로젝트가 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("프로젝트 대시보드 조회 성공")
    void getDashboard_success() throws Exception {
        // Given
        ProjectDashboardResponseDto dashboard = new ProjectDashboardResponseDto(
                1L, "Project", "2024-06-26T12:00", 5, 100, 0
        );

        when(projectService.getProjectDashboard(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(dashboard);

        // When & Then
        mockMvc.perform(get("/v1/project/1/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.projectName").value("Project"));
    }

    @Test
    @DisplayName("API 키 재발급 성공")
    void regenerateApiKey_success() throws Exception {
        // Given
        ApiKeyResponseDto apiKeyDto = new ApiKeyResponseDto("REGENERATED_KEY");

        when(projectService.regenerateApiKey(Mockito.eq(1L), Mockito.any()))
                .thenReturn(apiKeyDto);

        // When & Then
        mockMvc.perform(post("/v1/project/1/apikey/regenerate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKey").value("REGENERATED_KEY"));
    }
}