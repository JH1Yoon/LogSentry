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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "회원가입", description = "사용자 정보를 입력하여 회원가입을 진행합니다. 관리자로 가입하려면 adminKey가 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = SignupResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto,
            @Parameter(description = "관리자 키 (선택)") @RequestParam(required = false) String inputAdminKey
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequestDto, inputAdminKey));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "잘못된 인증 정보")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequestDto));
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserProfileResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getMyProfile(userDetails.getUser()));
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호를 입력하여 본인 확인 후 회원 탈퇴를 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치 또는 인증 오류")
    })
    @DeleteMapping("/withdraw")
    public ResponseEntity<SuccessResponse> withdrawUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody DeleteRequestDto deleteRequestDto
    ) {
        userService.withdraw(userDetails.getUser(), deleteRequestDto);
        return ResponseEntity.status(SuccessMessage.DELETED.getStatus())
                .body(new SuccessResponse(
                        SuccessMessage.DELETED.getStatus().value(),
                        SuccessMessage.DELETED.getMessage(userDetails.getUsername())
                ));
    }
}