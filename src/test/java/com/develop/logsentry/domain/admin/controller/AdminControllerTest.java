package com.develop.logsentry.domain.admin.controller;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.common.jwt.JwtUtil;
import com.develop.logsentry.domain.admin.dto.request.UserRoleUpdateRequestDto;
import com.develop.logsentry.domain.admin.dto.response.*;
import com.develop.logsentry.domain.admin.service.AdminService;
import com.develop.logsentry.domain.log.service.LogProducer;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaRepository jpaRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private LogProducer logProducer;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthFilter authFilter;

    @MockBean
    private AdminService adminService;

    @Test
    @DisplayName("전체 통계 조회_성공")
    void getOverview_success() throws Exception {
        // Given
        AdminOverviewResponseDto dto = new AdminOverviewResponseDto(10L, 5L, 100L);
        given(adminService.getOverview()).willReturn(dto);

        // When
        ResultActions result = mockMvc.perform(get("/v1/admin/overview"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.totalProjects").value(5))
                .andExpect(jsonPath("$.totalLogs").value(100));
    }

    @Test
    @DisplayName("일별 활성 사용자 조회_성공")
    void getDailyActiveUsers_success() throws Exception {
        // Given
        List<DailyActiveUserDto> mockList = List.of(
                new DailyActiveUserDto("2024-01-01", 10L),
                new DailyActiveUserDto("2024-01-02", 12L)
        );
        when(adminService.getDailyActiveUsers(any(), any())).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get("/v1/admin/daily-active-users")
                        .param("start", "2024-01-01T00:00:00")
                        .param("end", "2024-01-03T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @DisplayName("월별 프로젝트 활동 조회_성공")
    void getMonthlyProjectActivity_success() throws Exception {
        // Given
        List<MonthlyProjectActivityDto> mockList = List.of(
                new MonthlyProjectActivityDto("2024-01", 20L),
                new MonthlyProjectActivityDto("2024-02", 15L)
        );
        when(adminService.getMonthlyProjectActivity(any(), any())).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get("/v1/admin/monthly-project-activity")
                        .param("start", "2024-01-01T00:00:00")
                        .param("end", "2024-03-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("사용자 목록 조회_성공")
    void getUserList_success() throws Exception {
        // Given
        List<UserListResponseDto> users = List.of(
                new UserListResponseDto(1L, "test@example.com", "tester", UserRoleEnum.USER, true)
        );
        when(adminService.getUserList(eq("test@example.com"), isNull(), eq(0), eq(20)))
                .thenReturn(users);

        // When & Then
        mockMvc.perform(get("/v1/admin")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @DisplayName("사용자 상세 조회_성공")
    void getUserDetail_success() throws Exception {
        // Given
        UserDetailResponseDto dto = new UserDetailResponseDto(
                1L, "admin@example.com", "admin", UserRoleEnum.ADMIN, true,
                "2024-01-01T00:00:00", null);
        when(adminService.getUserDetail(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/v1/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    @DisplayName("사용자 역할 변경_성공")
    void updateUserRole_success() throws Exception {
        // Given
        UserRoleUpdateRequestDto dto = new UserRoleUpdateRequestDto("ADMIN");
        doNothing().when(adminService).updateUserRole(eq(1L), any());

        // When & Then
        mockMvc.perform(patch("/v1/admin/1/role")
                        .contentType("application/json")
                        .content("""
                            {
                                "newRole": "ADMIN"
                            }
                        """))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 비활성화_성공")
    void deactivateUser_success() throws Exception {
        // Given
        doNothing().when(adminService).deactivateUser(1L);

        // When & Then
        mockMvc.perform(patch("/v1/admin/1/deactivate"))
                .andExpect(status().isNoContent());
    }
}