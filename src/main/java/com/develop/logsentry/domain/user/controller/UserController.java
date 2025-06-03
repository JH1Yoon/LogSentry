package com.develop.logsentry.domain.user.controller;

import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.message.SuccessResponse;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.user.dto.request.DeleteRequestDto;
import com.develop.logsentry.domain.user.dto.request.LoginRequestDto;
import com.develop.logsentry.domain.user.dto.request.SignupRequestDto;
import com.develop.logsentry.domain.user.dto.response.LoginResponseDto;
import com.develop.logsentry.domain.user.dto.response.SignupResponseDto;
import com.develop.logsentry.domain.user.dto.response.UserProfileResponseDto;
import com.develop.logsentry.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto, @RequestParam(required = false) String inputAdminKey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequestDto, inputAdminKey));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequestDto));
    }

    // 마이페이지 조회
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getMyProfile(userDetails.getUser()));
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<SuccessResponse> withdrawUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteRequestDto deleteRequestDto) {
        userService.withdraw(userDetails.getUser(), deleteRequestDto);
        return ResponseEntity.status(SuccessMessage.DELETED.getStatus())
                .body(new SuccessResponse(SuccessMessage.DELETED.getStatus().value(), SuccessMessage.DELETED.getMessage(userDetails.getUsername())));
    }
}
