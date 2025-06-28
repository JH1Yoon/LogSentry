package com.develop.logsentry.domain.log.controller;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.dto.response.LogResponseDto;
import com.develop.logsentry.domain.log.dto.response.LogStatisticsDto;
import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.service.LogProducer;
import com.develop.logsentry.domain.log.service.LogService;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaRepository jpaRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private LogProducer logProducer;

    @MockBean
    private AuthFilter authFilter;

    @MockBean
    private LogService logService;

    private User mockUser;
    private LogResponseDto sampleLog;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "tester", "test@example.com", "123456", UserRoleEnum.USER, null, true, null);
        sampleLog = new LogResponseDto(
                1L,
                LogLevel.ERROR,
                "NullPointerException",
                "NULL_POINTER",
                "Something went wrong",
                "com.example.MyClass.method(MyClass.java:42)",
                LocalDateTime.of(2024, 6, 25, 12, 30)
        );

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("로그 조건 검색_성공")
    void getLogs_success() throws Exception {
        // Given
        Page<LogResponseDto> resultPage = new PageImpl<>(List.of(sampleLog));
        given(logService.getLogs(any(User.class), any(), any(), any(), any(), anyInt(), anyInt()))
                .willReturn(resultPage);

        // When & Then
        mockMvc.perform(get("/v1/log")
                        .param("start", "2024-06-01T00:00:00")
                        .param("end", "2024-06-30T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].logLevel").value("ERROR"))
                .andExpect(jsonPath("$.content[0].exceptionName").value("NullPointerException"));
    }

    @Test
    @DisplayName("로그 상세 조회_성공")
    void getLogDetail_success() throws Exception {
        // Given
        given(logService.getLogDetail(any(User.class), eq(1L))).willReturn(sampleLog);

        // When & Then
        mockMvc.perform(get("/v1/log/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logLevel").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    @DisplayName("로그 통계 조회_성공")
    void getStatistics_success() throws Exception {
        // Given
        List<LogStatisticsDto> stats = List.of(
                new LogStatisticsDto(LogLevel.ERROR, 7L),
                new LogStatisticsDto(LogLevel.INFO, 3L)
        );
        given(logService.getLogStatistics(any(User.class), any(), any(), any())).willReturn(stats);

        // When & Then
        mockMvc.perform(get("/v1/log/statistics")
                        .param("start", "2024-06-01T00:00:00")
                        .param("end", "2024-06-30T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].logLevel").value("ERROR"))
                .andExpect(jsonPath("$[0].count").value(7))
                .andExpect(jsonPath("$[1].logLevel").value("INFO"))
                .andExpect(jsonPath("$[1].count").value(3));
    }
}