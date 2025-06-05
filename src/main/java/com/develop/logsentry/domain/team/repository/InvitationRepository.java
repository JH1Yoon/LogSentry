package com.develop.logsentry.domain.team.repository;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.team.entity.Invitation;
import com.develop.logsentry.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    boolean existsByEmailAndTeam(String email, Team team);
    Optional<Invitation> findByInviteToken(String token);

    default void throwIfAlreadyInvited(String email, Team team) {
        if (existsByEmailAndTeam(email, team)) {
            throw new CustomException(ErrorCode.ALREADY_INVITED);
        }
    }

    default Invitation findByInviteTokenOrThrow(String token) {
        return findByInviteToken(token).orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITATION_TOKEN));
    }
}
