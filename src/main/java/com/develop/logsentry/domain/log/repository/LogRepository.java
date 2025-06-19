package com.develop.logsentry.domain.log.repository;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.entity.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    Optional<Log> findById(Long id);
    long countByProjectIdLegacyAndTimestampAfter(Long projectIdLegacy, LocalDateTime after);

    default Log findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.LOG_NOT_FOUND, null, "LOG", id));
    }

    // 전체 로그 조회
    Page<Log> findByLogLevelAndTimestampBetween(LogLevel logLevel, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Log> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 프로젝트별 필터링
    Page<Log> findByProjectIdLegacyAndLogLevelAndTimestampBetween(Long projectIdLegacy, LogLevel logLevel, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Log> findByProjectIdLegacyAndTimestampBetween(Long projectIdLegacy, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 전체 로그 레벨별 통계
    @Query("SELECT l.logLevel, COUNT(l) FROM Log l WHERE l.timestamp BETWEEN :start AND :end GROUP BY l.logLevel")
    List<Object[]> countGroupByLogLevel(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 프로젝트별 로그 레벨 통계
    @Query("SELECT l.logLevel, COUNT(l) FROM Log l WHERE l.projectIdLegacy = :projectId AND l.timestamp BETWEEN :start AND :end GROUP BY l.logLevel")
    List<Object[]> countGroupByProjectIdAndLogLevel(@Param("projectId") Long projectId,
                                                    @Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);
}
