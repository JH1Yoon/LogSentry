package com.develop.logsentry.domain.team.controller;

import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.team.dto.request.InviteRequestDto;
import com.develop.logsentry.domain.team.dto.request.TeamRequestDto;
import com.develop.logsentry.domain.team.dto.request.UpdateRoleRequestDto;
import com.develop.logsentry.domain.team.dto.response.TeamMemberResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamResponseDto;
import com.develop.logsentry.domain.team.dto.response.TeamSummaryResponseDto;
import com.develop.logsentry.domain.team.dto.response.UpdateMemberResponseDto;
import com.develop.logsentry.domain.team.service.TeamService;
import com.develop.logsentry.domain.user.entity.User;
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

    // 팀 생성
    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid TeamRequestDto teamRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(userDetails.getUser(), teamRequestDto));
    }

    // 팀 목록 조회
    @GetMapping
    public ResponseEntity<List<TeamSummaryResponseDto>> getMyTeams(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getMyTeams(userDetails.getUser()));
    }

    // 팀 초대 (이메일 기반)
    @PostMapping("/{teamId}/invite")
    public ResponseEntity<Void> inviteUserToTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody InviteRequestDto inviteRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        teamService.inviteUser(teamId, inviteRequestDto, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // 팀 멤버 목록 조회
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponseDto>> getTeamMembers(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamMembers(teamId, userDetails.getUser()));
    }

    // 권한 변경
    @PutMapping("/{teamId}/members/{userId}/role")
    public ResponseEntity<UpdateMemberResponseDto> updateMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestBody UpdateRoleRequestDto updateRoleRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeamMemberRole(teamId, userId, updateRoleRequestDto.getRole(), userDetails.getUser()));
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public void deleteTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        teamService.deleteTeam(teamId, userDetails.getUser());
    }
}