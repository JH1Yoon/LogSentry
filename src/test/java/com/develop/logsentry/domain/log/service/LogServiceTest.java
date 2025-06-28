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
import com.develop.logsentry.domain.team.entity.Team;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    @InjectMocks
    private LogService logService;

    @Mock
    LogRepository logRepository;

    @Mock
    ProjectRepository projectRepository;

    private User adminUser;
    private User normalUser;
    private Project project;
    private Log log;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logService = new LogService(logRepository, projectRepository);

        adminUser = new User(1L, "admin", "admin@test.com", "pass", UserRoleEnum.ADMIN, null, true, null);
        normalUser = new User(2L, "user", "user@test.com", "pass", UserRoleEnum.USER, null, true, null);

        project = Project.builder().id(100L).createdBy(adminUser).team(Team.builder().userTeams(Set.of()).build()).build();

        log = Log.builder()
                .id(1L)
                .logLevel(LogLevel.ERROR)
                .exceptionName("NullPointerException")
                .errorCodeMessage("NULL_POINTER")
                .message("Error occurred")
                .stackSummary("trace")
                .timestamp(LocalDateTime.now())
                .projectIdLegacy(project.getId())
                .build();
    }

    @Test
    @DisplayName("전체 로그 조회 성공 - 관리자")
    void getLogs_admin_success() {
        // Given
        Page<Log> mockPage = new PageImpl<>(List.of(log));
        when(logRepository.findByTimestampBetween(any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        Page<LogResponseDto> result = logService.getLogs(
                adminUser, null, null,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 0, 10
        );

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(logRepository).findByTimestampBetween(any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("전체 로그 조회 실패 - 관리자 아님")
    void getLogs_nonAdmin_fail() {
        // Given

        // When & Then
        assertThatThrownBy(() -> logService.getLogs(
                normalUser, null, null,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 0, 10))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("로그 상세 조회 성공 - 관리자")
    void getLogDetail_admin_success() {
        // Given
        ReflectionTestUtils.setField(log, "projectIdLegacy", 100L);
        when(logRepository.findByIdOrThrow(1L)).thenReturn(log);
        when(projectRepository.findAnyById(100L)).thenReturn(Optional.of(project));

        // When
        LogResponseDto response = logService.getLogDetail(adminUser, 1L);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그 상세 조회 실패 - 권한 없음")
    void getLogDetail_unauthorizedUser_fail() {
        // Given
        when(logRepository.findByIdOrThrow(1L)).thenReturn(log);
        when(projectRepository.findAnyById(project.getId())).thenReturn(Optional.of(project));

        // When & Then
        assertThatThrownBy(() -> logService.getLogDetail(normalUser, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("전체 로그 통계 조회 - 성공")
    void getLogStatistics_admin_success() {
        // Given
        Object[] record1 = new Object[]{LogLevel.ERROR, 5L};
        Object[] record2 = new Object[]{LogLevel.INFO, 2L};

        when(logRepository.countGroupByLogLevel(any(), any()))
                .thenReturn(List.of(record1, record2));

        // When
        List<LogStatisticsDto> result = logService.getLogStatistics(
                adminUser, null,
                LocalDateTime.now().minusDays(1), LocalDateTime.now()
        );

        // Then
        assertEquals(2, result.size());
        assertEquals(LogLevel.ERROR, result.get(0).getLogLevel());
        assertEquals(5L, result.get(0).getCount());
    }
}