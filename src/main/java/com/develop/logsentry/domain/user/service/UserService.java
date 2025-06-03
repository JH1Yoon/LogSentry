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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.key}")
    private String adminKey;

    /** 회원가입
     *
     * @param signupRequestDto
     * @param inputAdminKey
     * @return SignupResponseDto
     */
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto, String inputAdminKey) {
        String username = signupRequestDto.getUsername();
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        // 중복 사용자 확인
        userRepository.throwIfEmailExists(email);
        userRepository.throwIfUsernameExists(username);

        UserRoleEnum role = UserRoleEnum.USER;
        if (inputAdminKey != null) {
            if (inputAdminKey.equals(adminKey)) {
                role = UserRoleEnum.ADMIN;
            } else {
                throw new CustomException(ErrorCode.INVALID_ADMIN_KEY);
            }
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);
        return new SignupResponseDto(email, username, List.of(role));
    }

    /** 로그인
     *
     * @param loginRequestDto
     * @return LoginResponseDto
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmailAndIsActiveTrueOrThrow(loginRequestDto.getEmail());

        UserRoleEnum role = user.getRole();
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        String existPassword = user.getPassword();

        // 비밀번호 확인
        checkPassword(password, existPassword);

        return new LoginResponseDto(jwtUtil.createToken(email, password, role));
    }

    public UserProfileResponseDto getMyProfile(User user) {
        if (user == null) { throw new CustomException(ErrorCode.USER_NOT_FOUND);}

        return new UserProfileResponseDto(user.getId(), user.getEmail(), user.getUsername(), List.of(user.getRole()));
    }

    @Transactional
    public void withdraw(User user, DeleteRequestDto deleteRequestDto) {
        User activeUser = userRepository.findByEmailAndIsActiveTrueOrThrow(user.getEmail());

        checkPassword(deleteRequestDto.getPassword(), activeUser.getPassword());

        activeUser.deactivate();
    }

    // 비밀번호 확인

    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
