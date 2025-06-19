package com.develop.logsentry.domain.team.repository;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    Optional<Team> findById(Long id);

    default void throwIfNameExists(String name) {
        if (existsByName(name)) {
            throw new CustomException(ErrorCode.TEAM_ALREADY_EXISTS, null, name);
        }
    }

    default Team findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND, null, "TEAM"));
    }
}
