package com.develop.logsentry.domain.team.controller;

import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.message.SuccessResponse;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.team.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;

    @Operation(
            summary = "팀 초대 수락",
            description = "초대 토큰을 통해 로그인된 사용자가 팀 초대를 수락합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "초대 수락 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않거나 만료된 초대 토큰"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "초대 수락 권한 없음")
    })
    @PostMapping("/{token}/accept")
    public ResponseEntity<SuccessResponse> acceptInvitation(
            @Parameter(description = "초대 토큰", example = "abc123xyz") @PathVariable String token,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        invitationService.acceptInvitation(token, userDetails.getUser());

        return ResponseEntity.status(SuccessMessage.INVITATION_ACEEPT_SUCCESS.getStatus())
                .body(new SuccessResponse(
                        SuccessMessage.INVITATION_ACEEPT_SUCCESS.getStatus().value(),
                        SuccessMessage.INVITATION_ACEEPT_SUCCESS.getMessage())
                );
    }
}