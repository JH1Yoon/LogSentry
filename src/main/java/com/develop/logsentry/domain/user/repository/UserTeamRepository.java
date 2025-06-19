package com.develop.logsentry.domain.user.repository;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.user.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    Optional<UserTeam> findByTeamIdAndUserId(Long teamId, Long userId);
    List<UserTeam> findAllByUserId(Long userId);
    List<UserTeam> findAllByTeamId(Long teamId);
    int countByTeamId(Long teamId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    default UserTeam findByTeamIdAndUserIdOrThrow(Long teamId, Long userId) {
        return findByTeamIdAndUserId(teamId, userId).orElseThrow(() -> new CustomException(ErrorCode.TEAM_ACCESS_DENIED, null, "USER_TEAM"));
    }

    default void throwIfTeamIdAndUserIdExists(Long teamId, Long userId) {
        if(existsByTeamIdAndUserId(teamId, userId)) {
            throw new CustomException(ErrorCode.USER_ALREADY_IN_TEAM, null, "USER_TEAM");
        }
    }
}
