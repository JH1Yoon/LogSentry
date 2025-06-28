package com.develop.logsentry.domain.admin.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.admin.dto.request.UserRoleUpdateRequestDto;
import com.develop.logsentry.domain.admin.dto.response.AdminOverviewResponseDto;
import com.develop.logsentry.domain.admin.dto.response.DailyActiveUserDto;
import com.develop.logsentry.domain.admin.dto.response.MonthlyProjectActivityDto;
import com.develop.logsentry.domain.admin.dto.response.UserDetailResponseDto;
import com.develop.logsentry.domain.admin.repository.AdminRepository;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.develop.logsentry.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    AdminRepository adminRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("관리자용 전체 통계 조회_성공")
    void getOverview_success() {
        // Given
        when(adminRepository.countActiveUsers()).thenReturn(10L);
        when(adminRepository.countActiveProjects()).thenReturn(5L);
        when(adminRepository.countAllLogs()).thenReturn(100L);

        // When
        AdminOverviewResponseDto overview = adminService.getOverview();

        // Then
        assertThat(overview.getTotalUsers()).isEqualTo(10L);
        assertThat(overview.getTotalProjects()).isEqualTo(5L);
        assertThat(overview.getTotalLogs()).isEqualTo(100L);
    }

    @Test
    @DisplayName("일별 활성 사용자 수 조회 - 통계 정상 반환")
    void getDailyActiveUsers_success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 3, 0, 0);
        List<Object[]> mockResult = List.of(
                new Object[]{java.sql.Date.valueOf("2024-01-01"), 5L},
                new Object[]{java.sql.Date.valueOf("2024-01-02"), 8L}
        );
        when(adminRepository.countDailyActiveUsers(start, end)).thenReturn(mockResult);

        // When
        List<DailyActiveUserDto> result = adminService.getDailyActiveUsers(start, end);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDate()).isEqualTo("2024-01-01");
        assertThat(result.get(0).getActiveUserCount()).isEqualTo(5L);
        assertThat(result.get(1).getDate()).isEqualTo("2024-01-02");
        assertThat(result.get(1).getActiveUserCount()).isEqualTo(8L);
    }

    @Test
    @DisplayName("월별 프로젝트 활동 통계 조회 - 통계 정상 반환")
    void getMonthlyProjectActivity_success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 3, 0, 0);
        List<Object[]> mockResult = List.of(
                new Object[]{"2024-01", 12L},
                new Object[]{"2024-02", 7L}
        );
        when(adminRepository.countMonthlyProjectActivity(start, end)).thenReturn(mockResult);

        // When
        List<MonthlyProjectActivityDto> result = adminService.getMonthlyProjectActivity(start, end);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMonth()).isEqualTo("2024-01");
        assertThat(result.get(0).getCount()).isEqualTo(12L);
        assertThat(result.get(1).getMonth()).isEqualTo("2024-02");
        assertThat(result.get(1).getCount()).isEqualTo(7L);


    }

    @Test
    @DisplayName("사용자 목록 조회_성공")
    void getUserList_success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User(1L, "tester", "test@example.com", "123456", UserRoleEnum.USER, null, true, null);
        when(userRepository.findByEmailContainingIgnoreCase("test", pageable)).thenReturn(List.of(user));

        // When
        var result = adminService.getUserList("test", null, 0, 10);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("사용자 목록 조회 - 잘못된 역할 필터는 예외 발생")
    void getUserList_failed_withInvalidRole() {
        // Given
        String invalidRole = "INVALID";

        // When & Then
        assertThatThrownBy(() -> adminService.getUserList(null, invalidRole, 0, 10))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_ROLE.getMessage());
    }

    @Test
    @DisplayName("사용자 상세 조회 - 정상 응답 반환")
    void getUserDetail_success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "tester", "test@example.com", "123456", UserRoleEnum.USER, null, true, null);
        ReflectionTestUtils.setField(user, "createdAt", now);
        when(userRepository.findByIdAndIsActiveTrueOrThrow(1L)).thenReturn(user);

        // When
        UserDetailResponseDto detail = adminService.getUserDetail(1L);

        // Then
        assertThat(detail.getEmail()).isEqualTo("test@example.com");
        assertThat(detail.getUsername()).isEqualTo("tester");
        assertThat(detail.isActive()).isTrue();
        assertThat(detail.getCreatedAt()).isEqualTo(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    @DisplayName("사용자 역할 변경 - USER → ADMIN 성공")
    void updateUserRole_success() {
        // Given
        User user = new User(2L, "tester", "test@example.com", "123456", UserRoleEnum.USER, null, true, null);
        when(userRepository.findByIdAndIsActiveTrueOrThrow(2L)).thenReturn(user);
        UserRoleUpdateRequestDto dto = new UserRoleUpdateRequestDto("ADMIN");

        // When
        adminService.updateUserRole(2L, dto);

        // Then
        assertThat(user.getRole()).isEqualTo(UserRoleEnum.ADMIN);
        verify(userRepository).save(user);

    }

    @Test
    @DisplayName("사용자 비활성화 - 로그인 차단 성공")
    void deactivateUser_success() {
        // Given
        User user = new User(2L, "tester", "test@example.com", "123456", UserRoleEnum.USER, null, true, null);
        when(userRepository.findByIdAndIsActiveTrueOrThrow(2L)).thenReturn(user);

        // When
        adminService.deactivateUser(2L);

        // Then
        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }
}