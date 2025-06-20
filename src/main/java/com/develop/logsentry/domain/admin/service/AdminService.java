package com.develop.logsentry.domain.admin.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.admin.dto.request.UserRoleUpdateRequestDto;
import com.develop.logsentry.domain.admin.dto.response.*;
import com.develop.logsentry.domain.admin.repository.AdminRepository;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.develop.logsentry.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    /** 관리자용 전체 통계 조회
     *
     * @return AdminOverviewResponseDto
     */
    public AdminOverviewResponseDto getOverview() {
        return new AdminOverviewResponseDto(
                adminRepository.countActiveUsers(),
                adminRepository.countActiveProjects(),
                adminRepository.countAllLogs()
        );
    }

    /** 기간 내 일별 활성 사용자 수 조회
     *
     * @param start
     * @param end
     * @return List<DailyActiveUserDto>
     */
    public List<DailyActiveUserDto> getDailyActiveUsers(LocalDateTime start, LocalDateTime end) {
        return adminRepository.countDailyActiveUsers(start, end).stream()
                .map(record -> new DailyActiveUserDto(record[0].toString(), (Long) record[1]))
                .toList();
    }

    /** 기간 내 월별 프로젝트 활동 통계 조회
     *
     * @param start
     * @param end
     * @return List<MonthlyProjectActivityDto>
     */
    public List<MonthlyProjectActivityDto> getMonthlyProjectActivity(LocalDateTime start, LocalDateTime end) {
        return adminRepository.countMonthlyProjectActivity(start, end).stream()
                .map(record -> new MonthlyProjectActivityDto((String) record[0], (Long) record[1]))
                .toList();
    }

    /**	사용자 목록 조회 및 필터링
     *
     * @param email
     * @param roleStr
     * @param page
     * @param size
     * @return List<UserListResponseDto>
     */
    public List<UserListResponseDto> getUserList(String email, String roleStr, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<User> users;

        if (email != null) {
            users = userRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else if (roleStr != null) {
            try {
                UserRoleEnum role = UserRoleEnum.valueOf(roleStr.toUpperCase());
                users = userRepository.findByRole(role, pageable);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_ROLE, null);
            }
        } else {
            users = userRepository.findAll(pageable).getContent();
        }

        return users.stream()
                .map(user -> new UserListResponseDto(user.getId(), user.getEmail(), user.getUsername(), user.getRole(), user.isActive()))
                .toList();
    }

    /** 사용자 상세 정보 조회
     *
     * @param userId
     * @return UserDetailResponseDto
     */
    public UserDetailResponseDto getUserDetail(Long userId) {
        User user = userRepository.findByIdAndIsActiveTrueOrThrow(userId);

        return new UserDetailResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                user.getDeletedAt() != null ? user.getDeletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
        );
    }

    /**	사용자 역할 변경
     *
     * @param userId
     * @param request
     */
    @Transactional
    public void updateUserRole(Long userId, UserRoleUpdateRequestDto request) {
        User user = userRepository.findByIdAndIsActiveTrueOrThrow(userId);

        user.changeRole(UserRoleEnum.valueOf(request.getNewRole()));
        userRepository.save(user);
    }

    /** 사용자 비활성화(로그인 차단)
     *
     * @param userId
     */
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findByIdAndIsActiveTrueOrThrow(userId);

        user.deactivate();
        userRepository.save(user);
    }
}
