package com.develop.logsentry.domain.team.controller;

import com.develop.logsentry.common.message.SuccessMessage;
import com.develop.logsentry.common.message.SuccessResponse;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.team.dto.request.InviteRequestDto;
import com.develop.logsentry.domain.team.dto.request.TeamRequestDto;
import com.develop.logsentry.domain.team.dto.request.UpdateRoleRequestDto;
import com.develop.logsentry.domain.team.dto.response.TeamMemberResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamSummaryResponseDto;
import com.develop.logsentry.domain.team.dto.response.UpdateMemberResponseDto;
import com.develop.logsentry.domain.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/team")
public class TeamController {
    private final TeamService teamService;

    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "팀 생성 성공")
    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody TeamRequestDto teamRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(userDetails.getUser(), teamRequestDto));
    }

    @Operation(summary = "내 팀 목록 조회", description = "사용자가 속한 팀 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "팀 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<TeamSummaryResponseDto>> getMyTeams(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(teamService.getMyTeams(userDetails.getUser()));
    }

    @Operation(summary = "팀원 초대", description = "지정한 이메일로 팀 초대를 보냅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/{teamId}/invite")
    public ResponseEntity<SuccessResponse> inviteUserToTeam(
            @Parameter(description = "팀 ID") @PathVariable Long teamId,
            @Valid @RequestBody InviteRequestDto inviteRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        teamService.inviteUser(teamId, inviteRequestDto, userDetails.getUser());
        return ResponseEntity.status(SuccessMessage.INVITATION_SEND_SUCCESS.getStatus())
                .body(new SuccessResponse(
                        SuccessMessage.INVITATION_SEND_SUCCESS.getStatus().value(),
                        SuccessMessage.INVITATION_SEND_SUCCESS.getMessage()));
    }

    @Operation(summary = "팀 멤버 목록 조회", description = "지정된 팀의 모든 멤버를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "멤버 목록 조회 성공")
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponseDto>> getTeamMembers(
            @Parameter(description = "팀 ID") @PathVariable Long teamId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId, userDetails.getUser()));
    }

    @Operation(summary = "팀 멤버 역할 변경", description = "지정된 멤버의 팀 내 역할을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "역할 변경 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/{teamId}/members/{userId}/role")
    public ResponseEntity<UpdateMemberResponseDto> updateMemberRole(
            @Parameter(description = "팀 ID") @PathVariable Long teamId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @RequestBody UpdateRoleRequestDto updateRoleRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(teamService.updateTeamMemberRole(
                teamId, userId, updateRoleRequestDto.getRole(), userDetails.getUser()));
    }

    @Operation(summary = "팀 삭제", description = "지정된 팀을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "팀 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @DeleteMapping("/{teamId}")
    public ResponseEntity<SuccessResponse> deleteTeam(
            @Parameter(description = "팀 ID") @PathVariable Long teamId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        teamService.deleteTeam(teamId, userDetails.getUser());
        return ResponseEntity.status(SuccessMessage.DELETED.getStatus())
                .body(new SuccessResponse(
                        SuccessMessage.DELETED.getStatus().value(),
                        SuccessMessage.DELETED.getMessage("팀")));
    }
}