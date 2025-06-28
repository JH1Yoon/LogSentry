package com.develop.logsentry.domain.user.controller;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.common.jwt.JwtUtil;
import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.log.service.LogProducer;
import com.develop.logsentry.domain.user.dto.request.DeleteRequestDto;
import com.develop.logsentry.domain.user.dto.request.LoginRequestDto;
import com.develop.logsentry.domain.user.dto.request.SignupRequestDto;
import com.develop.logsentry.domain.user.dto.response.LoginResponseDto;
import com.develop.logsentry.domain.user.dto.response.SignupResponseDto;
import com.develop.logsentry.domain.user.dto.response.UserProfileResponseDto;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.develop.logsentry.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
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
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private UserDetailsImpl userDetails;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "tester", "test@test.com", "123456", UserRoleEnum.USER, null, true, null);

        userDetails = new UserDetailsImpl(mockUser);
        auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("회원가입 성공")
    void Signup_success() throws Exception {
        // Given
        SignupRequestDto request = new SignupRequestDto("test@test.com", "tester", "123456");
        SignupResponseDto response = new SignupResponseDto("test@test.com", "tester", List.of(UserRoleEnum.USER));
        Mockito.when(userService.signup(any(SignupRequestDto.class), eq(null)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.role[0]").value("USER"));
    }

    @Test
    @DisplayName("로그인 성공")
    void Login_success() throws Exception {
        // Given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "123456");
        LoginResponseDto response = new LoginResponseDto("access-token");
        Mockito.when(userService.login(any(LoginRequestDto.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"));
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void GetMyProfile_success() throws Exception {
        // Given
        UserProfileResponseDto responseDto = new UserProfileResponseDto(1L, "test@test.com", "tester", List.of(UserRoleEnum.USER));
        Mockito.when(userService.getMyProfile(any(User.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/v1/user/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.username").value("tester"));
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    @WithMockUser
    void WithdrawUser_success() throws Exception {
        // Given
        User user = User.builder().id(1L).email("test@test.com").username("tester").role(UserRoleEnum.USER).build();
        DeleteRequestDto deleteRequestDto = new DeleteRequestDto("123456");
        Mockito.doNothing().when(userService).withdraw(any(User.class), any(DeleteRequestDto.class));

        // When & Then
        mockMvc.perform(delete("/v1/user/withdraw")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessMessage.DELETED.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessMessage.DELETED.getMessage("test@test.com")));
    }
}