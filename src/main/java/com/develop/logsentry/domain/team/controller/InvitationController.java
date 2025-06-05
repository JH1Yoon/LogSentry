package com.develop.logsentry.domain.team.controller;

import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.message.SuccessResponse;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.team.service.InvitationService;
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

    // 초대 수락
    @PostMapping("/{token}/accept")
    public ResponseEntity<SuccessResponse> acceptInvitation(
            @PathVariable String token, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        invitationService.acceptInvitation(token, userDetails.getUser());
        return ResponseEntity.status(SuccessMessage.INVITATION_ACEEPT_SUCCESS.getStatus())
                .body(new SuccessResponse(SuccessMessage.INVITATION_ACEEPT_SUCCESS.getStatus().value(), SuccessMessage.INVITATION_ACEEPT_SUCCESS.getMessage()));
    }
}