package com.develop.logsentry.domain.user.service;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.common.jwt.JwtUtil;
import com.develop.logsentry.domain.user.dto.request.DeleteRequestDto;
import com.develop.logsentry.domain.user.dto.request.LoginRequestDto;
import com.develop.logsentry.domain.user.dto.request.SignupRequestDto;
import com.develop.logsentry.domain.user.dto.response.LoginResponseDto;
import com.develop.logsentry.domain.user.dto.response.SignupResponseDto;
import com.develop.logsentry.domain.user.dto.response.UserProfileResponseDto;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.entity.UserRoleEnum;
import com.develop.logsentry.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 - 일반 사용자")
    void Signup_success_asUser() {
        // Given
        SignupRequestDto request = new SignupRequestDto("test@test.com", "tester", "password");

        when(passwordEncoder.encode("password")).thenReturn("hashed");
        doNothing().when(userRepository).throwIfEmailExists("test@test.com");
        doNothing().when(userRepository).throwIfUsernameExists("tester");

        // When
        SignupResponseDto response = userService.signup(request, null);

        // Then
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getUsername()).isEqualTo("tester");
        assertThat(response.getRole()).contains(UserRoleEnum.USER);
    }

    @Test
    @DisplayName("회원가입 성공 - 어드민 계정")
    void Signup_success_asAdmin() {
        // Given
        SignupRequestDto request = new SignupRequestDto("admin@test.com", "adminuser", "adminpass");

        when(passwordEncoder.encode("adminpass")).thenReturn("hashed-admin");
        doNothing().when(userRepository).throwIfEmailExists("admin@test.com");
        doNothing().when(userRepository).throwIfUsernameExists("adminuser");


        ReflectionTestUtils.setField(userService, "adminKey", "secret-key");

        // When
        SignupResponseDto response = userService.signup(request, "secret-key");

        // Then
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getUsername()).isEqualTo("adminuser");
        assertThat(response.getRole()).contains(UserRoleEnum.ADMIN);
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 admin key")
    void Signup_fail_invalid_admin_key() {
        // Given
        SignupRequestDto request = new SignupRequestDto("test@test.com", "tester", "password");

        // Then
        assertThatThrownBy(() -> userService.signup(request, "wrong-key"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_ADMIN_KEY.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void Login_success() {
        // Given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "1234");
        User user = User.builder()
                .email("test@test.com")
                .password("encoded")
                .role(UserRoleEnum.USER)
                .isActive(true)
                .build();

        when(userRepository.findByEmailAndIsActiveTrueOrThrow("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);
        when(jwtUtil.createToken("test@test.com", "1234", UserRoleEnum.USER)).thenReturn("token");

        // When
        LoginResponseDto response = userService.login(request);

        // Then
        assertThat(response.getToken()).isEqualTo("token");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void Login_fail_wrong_password() {
        // Given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "wrong");
        User user = User.builder()
                .email("test@test.com")
                .password("encoded")
                .role(UserRoleEnum.USER)
                .isActive(true)
                .build();

        when(userRepository.findByEmailAndIsActiveTrueOrThrow("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        // Then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void GetMyProfile_success() {
        // Given
        User user = User.builder().id(1L).email("a@a.com").username("tester").role(UserRoleEnum.USER).build();

        // When
        UserProfileResponseDto response = userService.getMyProfile(user);

        // Then
        assertThat(response.getEmail()).isEqualTo("a@a.com");
        assertThat(response.getUsername()).isEqualTo("tester");
        assertThat(response.getRole()).contains(UserRoleEnum.USER);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void Withdraw_success() {
        // Given
        User user = User.builder().email("test@test.com").password("encoded").build();
        DeleteRequestDto request = new DeleteRequestDto("1234");

        when(userRepository.findByEmailAndIsActiveTrueOrThrow("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);

        // When
        userService.withdraw(user, request);

        // Then
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 틀림")
    void Withdraw_fail_wrong_password() {
        // Given
        User user = User.builder().email("test@test.com").password("encoded").build();
        DeleteRequestDto request = new DeleteRequestDto("wrong");

        when(userRepository.findByEmailAndIsActiveTrueOrThrow("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        // Then
        assertThatThrownBy(() -> userService.withdraw(user, request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }
}