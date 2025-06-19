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
import com.develop.logsentry.domain.user.entity.UserTeam;
import com.develop.logsentry.domain.user.repository.UserRepository;
import com.develop.logsentry.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final InvitationRepository invitationRepository;

    /**
     * 팀 생성
     *
     * @param user
     * @param teamRequestDto
     * @return TeamResponseDto
     */
    @Transactional
    public TeamResponseDto createTeam(User user, TeamRequestDto teamRequestDto) {
        User activeUser = userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());

        teamRepository.throwIfNameExists(teamRequestDto.getName());


        Team team = Team.builder()
                .name(teamRequestDto.getName())
                .description(teamRequestDto.getDescription())
                .build();

        teamRepository.save(team);

        UserTeam userTeam = UserTeam.builder()
                .user(activeUser)
                .team(team)
                .role(TeamRole.ADMIN)
                .build();

        userTeamRepository.save(userTeam);

        return new TeamResponseDto(team.getId(), team.getName(), team.getDescription(), team.getCreatedAt());
    }

    /**
     * 팀 목록 조회
     *
     * @param user
     * @return
     */
    public List<TeamSummaryResponseDto> getMyTeams(User user) {
        User activeUser = userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());
        List<UserTeam> userTeams = userTeamRepository.findAllByUserId(activeUser.getId());

        return userTeams.stream()
                .map(ut -> TeamSummaryResponseDto.builder()
                        .teamId(ut.getTeam().getId())
                        .name(ut.getTeam().getName())
                        .description(ut.getTeam().getDescription())
                        .role(ut.getRole())
                        .build()).toList();
    }

    /**
     * 팀 초대 (이메일 기반)
     *
     * @param teamId
     * @param inviteRequestDto
     * @param inviter
     */
    @Transactional
    public void inviteUser(Long teamId, InviteRequestDto inviteRequestDto, User inviter) {
        Team team = teamRepository.findByIdOrThrow(teamId);
        UserTeam inviterTeamRole = userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, inviter.getId());

        if (inviterTeamRole.getRole() != TeamRole.ADMIN) {
            throw new CustomException(ErrorCode.NO_TEAM_ADMIN_PRIVILEGE, null, "TEAM");
        }

        Optional<User> optionalUser = userRepository.findByEmailAndIsActiveTrue(inviteRequestDto.getEmail());

        if (optionalUser.isPresent()) {
            User targetUser = optionalUser.get();

            userTeamRepository.throwIfTeamIdAndUserIdExists(teamId, targetUser.getId());

            userTeamRepository.save(UserTeam.builder()
                    .team(team)
                    .user(targetUser)
                    .role(TeamRole.MEMBER)
                    .build());

            return;
        }

        invitationRepository.throwIfAlreadyInvited(inviteRequestDto.getEmail(), team);

        invitationRepository.save(Invitation.builder()
                .email(inviteRequestDto.getEmail())
                .inviteToken(UUID.randomUUID().toString())
                .team(team)
                .accepted(false)
                .build());
    }

    /**
     * 팀 멤버 목록 조회
     *
     * @param teamId
     * @param requester
     * @return List<TeamMemberResponseDto>
     */
    public List<TeamMemberResponseDto> getTeamMembers(Long teamId, User requester) {
        userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, requester.getId());

        List<UserTeam> members = userTeamRepository.findAllByTeamId(teamId);

        return members.stream()
                .map(userTeam -> new TeamMemberResponseDto(userTeam.getUser(), userTeam.getRole()))
                .toList();
    }

    /**
     * 권한 변경
     *
     * @param teamId
     * @param targetUserId
     * @param user
     * @param newRole
     * @return
     */
    @Transactional
    public UpdateMemberResponseDto updateTeamMemberRole(Long teamId, Long targetUserId, String newRole, User user) {
        UserTeam admin = userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, user.getId());
        if (admin.getRole() != TeamRole.ADMIN) {
            throw new CustomException(ErrorCode.NO_TEAM_ADMIN_PRIVILEGE, null, "TEAM");
        }

        UserTeam target = userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, targetUserId);
        target.changeRole(TeamRole.valueOf(newRole));

        return new UpdateMemberResponseDto(target.getUser().getEmail(), target.getUser().getUsername(), target.getRole());
    }

    /**
     * 팀 삭제
     *
     * @param teamId
     * @param adminUser
     */
    @Transactional
    public void deleteTeam(Long teamId, User adminUser) {
        UserTeam admin = userTeamRepository.findByTeamIdAndUserIdOrThrow(teamId, adminUser.getId());
        if (admin.getRole() != TeamRole.ADMIN) {
            throw new CustomException(ErrorCode.NO_TEAM_ADMIN_PRIVILEGE, null, "TEAM");
        }

        Team team = teamRepository.findByIdOrThrow(teamId);
        teamRepository.delete(team);
    }
}
