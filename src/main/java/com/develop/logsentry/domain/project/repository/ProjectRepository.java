package com.develop.logsentry.domain.project.repository;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeamId(Long teamId);
    boolean existsByIdAndCreatedById(Long projectId, Long userId);
    Optional<Project> findByIdAndIsActiveTrue(Long projectId);

    default Project findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND, id));
    }

    default void existsByIdAndCreatedByIdOrThrow(Long projectId, Long userId) {
        if (!existsByIdAndCreatedById(projectId, userId)) {
            throw new CustomException(ErrorCode.NO_PROJECT_OWNER_PRIVILEGE);
        }
    }

    default Project findByIdAndIsActiveTrueOrThrow(Long projectId) {
        return findByIdAndIsActiveTrue(projectId).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_INACTIVE));
    }
}