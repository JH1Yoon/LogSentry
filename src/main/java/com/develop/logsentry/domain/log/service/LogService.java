package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.log.dto.response.LogResponseDto;
import com.develop.logsentry.domain.log.dto.response.LogStatisticsDto;
import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.repository.LogRepository;
import com.develop.logsentry.domain.project.entity.Project;
import com.develop.logsentry.domain.project.repository.ProjectRepository;
import com.develop.logsentry.domain.team.entity.TeamRole;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

    private final LogRepository logRepository;
    private final ProjectRepository projectRepository;

    /** 로그 조건 검색
     *
     * @param user
     * @param projectId
     * @param logLevel
     * @param start
     * @param end
     * @param page
     * @param size
     * @return Page<LogResponseDto>
     */
    public Page<LogResponseDto> getLogs(User user, Long projectId, LogLevel logLevel, LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Log> logs;

        if (projectId == null) {
            checkAdminRole(user);

            logs = (logLevel != null)
                    ? logRepository.findByLogLevelAndTimestampBetween(logLevel, start, end, pageable)
                    : logRepository.findByTimestampBetween(start, end, pageable);

        } else {
            Project project = getProjectAllowDeleted(projectId);
            checkAccessToProjectLogs(user, project);

            logs = (logLevel != null)
                    ? logRepository.findByProjectIdLegacyAndLogLevelAndTimestampBetween(projectId, logLevel, start, end, pageable)
                    : logRepository.findByProjectIdLegacyAndTimestampBetween(projectId, start, end, pageable);
        }

        return logs.map(LogResponseDto::from);
    }

    /** 로그 상세 조회
     *
     * @param user
     * @param logId
     * @return LogResponseDto
     */
    public LogResponseDto getLogDetail(User user, Long logId) {
        Log log = logRepository.findByIdOrThrow(logId);

        Long projectId = log.getProjectIdLegacy();

        if (projectId == null) {
            checkAdminRole(user); // 프로젝트 없으면 관리자만 조회 가능
            return LogResponseDto.from(log);
        }

        Project project = getProjectAllowDeleted(projectId);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            return LogResponseDto.from(log); // 서버 관리자는 무조건 가능
        }

        checkAccessToProjectLogs(user, project);

        return LogResponseDto.from(log);
    }

    /** 로그 통계 조회
     *
     * @param user
     * @param projectId
     * @param start
     * @param end
     * @return List<LogStatisticsDto>
     */
    public List<LogStatisticsDto> getLogStatistics(User user, Long projectId, LocalDateTime start, LocalDateTime end) {
        if (projectId == null) {
            checkAdminRole(user); // 전체 통계 조회는 관리자만 가능

            return logRepository.countGroupByLogLevel(start, end)
                    .stream()
                    .map(record -> new LogStatisticsDto((LogLevel) record[0], (Long) record[1]))
                    .toList();
        } else {
            Project project = getProjectAllowDeleted(projectId);

            checkAccessToProjectLogs(user, project);

            return logRepository.countGroupByProjectIdAndLogLevel(projectId, start, end)
                    .stream()
                    .map(record -> new LogStatisticsDto((LogLevel) record[0], (Long) record[1]))
                    .toList();
        }
    }

    /** 프로젝트 조회 (삭제된 프로젝트 포함) */
    private Project getProjectAllowDeleted(Long projectId) {
        return projectRepository.findAnyById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND, projectId, "PROJECT"));
    }

    /** 관리자 여부 체크, 아니면 예외 던짐 */
    private void checkAdminRole(User user) {
        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, null);
        }
    }

    /** 프로젝트 로그 접근 권한 체크 */
    private void checkAccessToProjectLogs(User user, Project project) {
        Long userId = user.getId();

        boolean isProjectCreator = project.getCreatedBy().getId().equals(userId);
        boolean isAdmin = user.getRole().equals(UserRoleEnum.ADMIN);
        boolean isTeamAdmin = project.getTeam().getUserTeams().stream()
                .anyMatch(userTeam -> userTeam.getUser().getId().equals(userId) && userTeam.getRole() == TeamRole.ADMIN);

        if (!(isProjectCreator || isAdmin || isTeamAdmin)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, null);
        }
    }
}
