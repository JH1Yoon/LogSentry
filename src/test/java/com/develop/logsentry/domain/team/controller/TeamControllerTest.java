package com.develop.logsentry.domain.team.controller;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.service.LogProducer;
import com.develop.logsentry.domain.team.dto.request.InviteRequestDto;
import com.develop.logsentry.domain.team.dto.request.TeamRequestDto;
import com.develop.logsentry.domain.team.dto.request.UpdateRoleRequestDto;
import com.develop.logsentry.domain.team.dto.response.TeamMemberResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamSummaryResponseDto;
import com.develop.logsentry.domain.team.dto.response.UpdateMemberResponseDto;
import com.develop.logsentry.domain.team.entity.TeamRole;
import com.develop.logsentry.domain.team.service.TeamService;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {
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
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "user", "user@example.com", "pass", UserRoleEnum.USER, null, true, null);
        adminUser = new User(2L, "admin", "admin@example.com", "pass", UserRoleEnum.ADMIN, null, true, null);

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    @DisplayName("팀 생성 - 성공")
    void createTeam_success() throws Exception {
        // Given
        TeamRequestDto requestDto = new TeamRequestDto("Team A", "Team A Description");
        TeamResponseDto responseDto = new TeamResponseDto(1L, "Team A", "Team A Description", LocalDateTime.now());
        when(teamService.createTeam(any(), any())).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value(1L))
                .andExpect(jsonPath("$.name").value("Team A"))
                .andExpect(jsonPath("$.description").value("Team A Description"));

        verify(teamService, times(1)).createTeam(any(), any());
    }

    @Test
    @DisplayName("내 팀 목록 조회 - 성공")
    void getMyTeams_success() throws Exception {
        // Given
        TeamSummaryResponseDto teamSummary1 = TeamSummaryResponseDto.builder()
                .teamId(1L)
                .name("Team A")
                .description("Team A Description")
                .role(TeamRole.ADMIN)
                .build();
        TeamSummaryResponseDto teamSummary2 = TeamSummaryResponseDto.builder()
                .teamId(2L)
                .name("Team B")
                .description("Team B Description")
                .role(TeamRole.MEMBER)
                .build();
        List<TeamSummaryResponseDto> teams = List.of(teamSummary1, teamSummary2);
        when(teamService.getMyTeams(any())).thenReturn(teams);

        // When & Then
        mockMvc.perform(get("/v1/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].teamId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Team A"))
                .andExpect(jsonPath("$[0].description").value("Team A Description"))
                .andExpect(jsonPath("$[1].teamId").value(2L))
                .andExpect(jsonPath("$[1].name").value("Team B"))
                .andExpect(jsonPath("$[1].description").value("Team B Description"));

        verify(teamService, times(1)).getMyTeams(any());
    }

    @Test
    @DisplayName("팀 초대 - 성공")
    void inviteUserToTeam_success() throws Exception {
        // Given
        InviteRequestDto inviteRequest = new InviteRequestDto("invitee@example.com");
        doNothing().when(teamService).inviteUser(eq(1L), any(InviteRequestDto.class), any(User.class));

        // When & Then
        mockMvc.perform(post("/v1/team/1/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessMessage.INVITATION_SEND_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessMessage.INVITATION_SEND_SUCCESS.getMessage()));

        verify(teamService, times(1)).inviteUser(eq(1L), any(InviteRequestDto.class), any(User.class));
    }

    @Test
    @DisplayName("팀 멤버 목록 조회 - 성공")
    void getTeamMembers_success() throws Exception {
        // Given
        List<TeamMemberResponseDto> members = List.of(
                new TeamMemberResponseDto("member1@example.com", "member1", TeamRole.MEMBER),
                new TeamMemberResponseDto("member2@example.com", "member2", TeamRole.ADMIN)
        );
        when(teamService.getTeamMembers(eq(1L), any())).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/v1/team/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("member1@example.com"))
                .andExpect(jsonPath("$[0].username").value("member1"))
                .andExpect(jsonPath("$[0].role").value("MEMBER"))
                .andExpect(jsonPath("$[1].email").value("member2@example.com"))
                .andExpect(jsonPath("$[1].username").value("member2"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));

        verify(teamService, times(1)).getTeamMembers(eq(1L), any());
    }

    @Test
    @DisplayName("팀 멤버 권한 변경 - 성공")
    void updateMemberRole_success() throws Exception {
        // Given
        UpdateRoleRequestDto roleRequest = new UpdateRoleRequestDto("ADMIN");
        UpdateMemberResponseDto responseDto = new UpdateMemberResponseDto("updatemember@example.com", "updatemember", TeamRole.ADMIN);
        when(teamService.updateTeamMemberRole(eq(1L), eq(2L), eq("ADMIN"), any())).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/v1/team/1/members/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updatemember@example.com"))
                .andExpect(jsonPath("$.name").value("updatemember"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(teamService, times(1)).updateTeamMemberRole(eq(1L), eq(2L), eq("ADMIN"), any());
    }

    @Test
    @DisplayName("팀 삭제 - 성공")
    void deleteTeam_success() throws Exception {
        // Given
        doNothing().when(teamService).deleteTeam(eq(1L), any());

        // When & Then
        mockMvc.perform(delete("/v1/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessMessage.DELETED.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessMessage.DELETED.getMessage("팀")));

        verify(teamService, times(1)).deleteTeam(eq(1L), any());
    }
}