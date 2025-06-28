package com.develop.logsentry.domain.team.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.team.entity.Invitation;
import com.develop.logsentry.domain.team.entity.Team;
import com.develop.logsentry.domain.team.repository.InvitationRepository;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.develop.logsentry.domain.user.entity.UserTeam;
import com.develop.logsentry.domain.user.repository.UserTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {
    @InjectMocks
    InvitationService invitationService;

    @Mock
    InvitationRepository invitationRepository;

    @Mock
    UserTeamRepository userTeamRepository;

    private User mockUser;
    private Invitation mockInvitation;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "tester", "test@example.com", "1234", UserRoleEnum.USER, null, true, null);
        mockInvitation = Invitation.builder()
                .id(1L)
                .email("test@example.com")
                .inviteToken("valid-token")
                .accepted(false)
                .team(Team.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("초대 수락 - 성공")
    void acceptInvitation_success() {
        // Given
        when(invitationRepository.findByInviteTokenOrThrow("valid-token")).thenReturn(mockInvitation);
        doNothing().when(userTeamRepository).throwIfTeamIdAndUserIdExists(1L, 1L);
        when(userTeamRepository.save(any(UserTeam.class))).thenAnswer(i -> i.getArgument(0));

        // When
        invitationService.acceptInvitation("valid-token", mockUser);

        // Then
        verify(invitationRepository).findByInviteTokenOrThrow("valid-token");
        verify(userTeamRepository).throwIfTeamIdAndUserIdExists(1L, 1L);
        verify(userTeamRepository).save(any(UserTeam.class));
        verify(invitationRepository).delete(mockInvitation);
        assertThat(mockInvitation.isAccepted()).isTrue();
    }

    @Test
    @DisplayName("초대 수락 - 이미 수락된 초대일 경우 예외 발생")
    void acceptInvitation_alreadyAccepted_throws() {
        // Given
        mockInvitation.accept();
        when(invitationRepository.findByInviteTokenOrThrow("valid-token")).thenReturn(mockInvitation);

        // When & Then
        CustomException ex = assertThrows(CustomException.class,
                () -> invitationService.acceptInvitation("valid-token", mockUser));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_INVITED);
    }

    @Test
    @DisplayName("초대 수락 - 이메일이 초대 이메일과 다를 경우 예외 발생")
    void acceptInvitation_emailNotMatch_throws() {
        // Given
        when(invitationRepository.findByInviteTokenOrThrow("valid-token")).thenReturn(mockInvitation);
        User otherUser = new User(2L, "other", "other@example.com", "pass", UserRoleEnum.USER, null, true, null);

        // When & Then
        CustomException ex = assertThrows(CustomException.class,
                () -> invitationService.acceptInvitation("valid-token", otherUser));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_MATCH);
    }
}