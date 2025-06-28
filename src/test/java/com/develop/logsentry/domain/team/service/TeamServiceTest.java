package com.develop.logsentry.domain.team.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.team.dto.request.InviteRequestDto;
import com.develop.logsentry.domain.team.dto.request.TeamRequestDto;
import com.develop.logsentry.domain.team.dto.response.TeamMemberResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamSummaryResponseDto;
import com.develop.logsentry.domain.team.dto.response.UpdateMemberResponseDto;
import com.develop.logsentry.domain.team.entity.Invitation;
import com.develop.logsentry.domain.team.entity.Team;
import com.develop.logsentry.domain.team.entity.TeamRole;
import com.develop.logsentry.domain.team.repository.InvitationRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @InjectMocks
    TeamService teamService;

    @Mock
    TeamRepository teamRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserTeamRepository userTeamRepository;

    @Mock
    InvitationRepository invitationRepository;

    private User mockUser;
    private Team mockTeam;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "tester", "test@example.com", "1234", UserRoleEnum.USER, null, true, null);
        mockTeam = Team.builder().id(1L).name("Test Team").description("Test Team Description").createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("팀 생성 - 성공")
    void createTeam_success() {
        // Given
        TeamRequestDto dto = new TeamRequestDto("New Team", "New Team Description");
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(mockUser.getEmail())).thenReturn(mockUser);
        Team savedTeam = Team.builder().id(100L).name(dto.getName()).description(dto.getDescription()).build();
        when(teamRepository.save(any())).thenReturn(savedTeam);

        // When
        TeamResponseDto response = teamService.createTeam(mockUser, dto);

        // Then
        assertThat(response.getName()).isEqualTo("New Team");
        verify(teamRepository).throwIfNameExists("New Team");
        verify(userTeamRepository).save(any());
    }

    @Test
    @DisplayName("내 팀 목록 조회 - 성공")
    void getMyTeams_success() {
        // Given
        when(userRepository.findByEmailAndIsActiveTrueOrThrow(mockUser.getEmail())).thenReturn(mockUser);
        Team team = Team.builder().id(1L).name("Team A").description("Desc").build();
        UserTeam userTeam = UserTeam.builder().user(mockUser).team(team).role(TeamRole.ADMIN).build();
        when(userTeamRepository.findAllByUserId(mockUser.getId())).thenReturn(List.of(userTeam));

        // When
        List<TeamSummaryResponseDto> result = teamService.getMyTeams(mockUser);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Team A");
        verify(userRepository).findByEmailAndIsActiveTrueOrThrow(mockUser.getEmail());
        verify(userTeamRepository).findAllByUserId(mockUser.getId());
    }


    @Test
    @DisplayName("팀 초대 - 유저 존재하고 팀에 없는 경우 초대 성공")
    void inviteUser_existingUser_notInTeam_success() {
        // Given
        InviteRequestDto dto = new InviteRequestDto("invitee@email.com");
        User invitee = new User(2L, "invitee", dto.getEmail(), "pass", null, null, true, null);
        UserTeam admin = UserTeam.builder().user(mockUser).team(mockTeam).role(TeamRole.ADMIN).build();

        when(teamRepository.findByIdOrThrow(1L)).thenReturn(mockTeam);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, mockUser.getId())).thenReturn(admin);
        when(userRepository.findByEmailAndIsActiveTrue(dto.getEmail())).thenReturn(Optional.of(invitee));

        // When
        teamService.inviteUser(1L, dto, mockUser);

        // Then
        verify(userTeamRepository).throwIfTeamIdAndUserIdExists(1L, 2L);
        verify(userTeamRepository).save(any());
    }

    @Test
    @DisplayName("팀 초대 - 유저 없으면 초대 토큰 생성")
    void inviteUser_nonExistingUser_createsInvitation() {
        // Given
        InviteRequestDto dto = new InviteRequestDto("new@email.com");
        UserTeam admin = UserTeam.builder().user(mockUser).team(mockTeam).role(TeamRole.ADMIN).build();

        when(teamRepository.findByIdOrThrow(1L)).thenReturn(mockTeam);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, mockUser.getId())).thenReturn(admin);
        when(userRepository.findByEmailAndIsActiveTrue(dto.getEmail())).thenReturn(Optional.empty());

        // When
        teamService.inviteUser(1L, dto, mockUser);

        // Then
        verify(invitationRepository).throwIfAlreadyInvited(dto.getEmail(), mockTeam);
        verify(invitationRepository).save(any(Invitation.class));
    }

    @Test
    @DisplayName("팀 초대 - 관리자 권한 없을 경우 예외 발생")
    void inviteUser_notAdmin_throwsException() {
        // Given
        InviteRequestDto dto = new InviteRequestDto("target@email.com");
        UserTeam member = UserTeam.builder().user(mockUser).team(mockTeam).role(TeamRole.MEMBER).build();

        when(teamRepository.findByIdOrThrow(1L)).thenReturn(mockTeam);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, mockUser.getId())).thenReturn(member);

        // When & Then
        assertThatThrownBy(() -> teamService.inviteUser(1L, dto, mockUser))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NO_TEAM_ADMIN_PRIVILEGE.getMessage());
    }

    @Test
    @DisplayName("팀 멤버 목록 조회 - 성공")
    void getTeamMembers_success() {
        // Given
        UserTeam self = UserTeam.builder().team(mockTeam).user(mockUser).role(TeamRole.ADMIN).build();
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, 1L)).thenReturn(self);
        when(userTeamRepository.findAllByTeamId(1L)).thenReturn(List.of(self));

        // When
        List<TeamMemberResponseDto> result = teamService.getTeamMembers(1L, mockUser);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(mockUser.getEmail());
    }

    @Test
    @DisplayName("팀 멤버 권한 수정 - 성공")
    void updateTeamMemberRole_success() {
        // Given
        UserTeam admin = UserTeam.builder().user(mockUser).team(mockTeam).role(TeamRole.ADMIN).build();
        User targetUser = new User(2L, "target", "target@email.com", "pass", null, null, true, null);
        UserTeam target = UserTeam.builder().user(targetUser).team(mockTeam).role(TeamRole.MEMBER).build();

        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, 1L)).thenReturn(admin);
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, 2L)).thenReturn(target);

        // When
        UpdateMemberResponseDto response = teamService.updateTeamMemberRole(1L, 2L, "ADMIN", mockUser);

        // Then
        assertThat(response.getRole()).isEqualTo(TeamRole.ADMIN);
    }

    @Test
    @DisplayName("팀 멤버 권한 변경 - 실패 (관리자 권한 아님)")
    void updateTeamMemberRole_fail_not_admin() {
        // Given
        UserTeam nonAdminUserTeam = UserTeam.builder()
                .user(mockUser)
                .team(mockTeam)
                .role(TeamRole.MEMBER)  // ADMIN 아님
                .build();

        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(mockTeam.getId(), mockUser.getId()))
                .thenReturn(nonAdminUserTeam);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> teamService.updateTeamMemberRole(mockTeam.getId(), 2L, "ADMIN", mockUser));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_TEAM_ADMIN_PRIVILEGE);
        assertThat(exception.getSource()).isEqualTo("team");

        verify(userTeamRepository).findByTeamIdAndUserIdOrThrow(mockTeam.getId(), mockUser.getId());
        verify(userTeamRepository, never()).findByTeamIdAndUserIdOrThrow(eq(mockTeam.getId()), eq(2L));
    }

    @Test
    @DisplayName("팀 삭제 - 성공")
    void deleteTeam_success() {
        // Given
        UserTeam admin = UserTeam.builder().user(mockUser).team(mockTeam).role(TeamRole.ADMIN).build();
        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(1L, mockUser.getId())).thenReturn(admin);
        when(teamRepository.findByIdOrThrow(1L)).thenReturn(mockTeam);

        // When
        teamService.deleteTeam(1L, mockUser);

        // Then
        verify(teamRepository).delete(mockTeam);
    }

    @Test
    @DisplayName("팀 삭제 - 실패 (관리자 권한 아님)")
    void deleteTeam_fail_not_admin() {
        // Given
        UserTeam memberUserTeam = UserTeam.builder()
                .user(mockUser)
                .team(mockTeam)
                .role(TeamRole.MEMBER)
                .build();

        when(userTeamRepository.findByTeamIdAndUserIdOrThrow(mockTeam.getId(), mockUser.getId()))
                .thenReturn(memberUserTeam);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> teamService.deleteTeam(mockTeam.getId(), mockUser));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_TEAM_ADMIN_PRIVILEGE);
        assertThat(exception.getSource()).isEqualTo("team");

        verify(userTeamRepository).findByTeamIdAndUserIdOrThrow(mockTeam.getId(), mockUser.getId());
        verify(teamRepository, never()).delete(any());
    }
}