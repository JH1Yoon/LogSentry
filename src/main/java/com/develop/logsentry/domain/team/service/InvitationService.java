package com.develop.logsentry.domain.team.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.team.entity.Invitation;
import com.develop.logsentry.domain.team.entity.TeamRole;
import com.develop.logsentry.domain.team.repository.InvitationRepository;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserTeam;
import com.develop.logsentry.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserTeamRepository userTeamRepository;

    /** 초대 수락
     *
     * @param token
     * @param user
     */
    @Transactional
    public void acceptInvitation(String token, User user) {
        Invitation invitation = invitationRepository.findByInviteTokenOrThrow(token);

        if (invitation.isAccepted()) {
            throw new CustomException(ErrorCode.ALREADY_INVITED, "INVITATION");
        }

        if (!invitation.getEmail().equals(user.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_MATCH, "INVITATION");
        }

        userTeamRepository.throwIfTeamIdAndUserIdExists(invitation.getTeam().getId(), user.getId());

        userTeamRepository.save(UserTeam.builder()
                .team(invitation.getTeam())
                .user(user)
                .role(TeamRole.MEMBER)
                .build());

        invitation.accept();
        invitationRepository.delete(invitation);
    }
}
