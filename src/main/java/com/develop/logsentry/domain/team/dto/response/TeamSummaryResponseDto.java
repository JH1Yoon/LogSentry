package com.develop.logsentry.domain.team.dto.response;

import com.develop.logsentry.domain.team.entity.TeamRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamSummaryResponseDto {
    private Long teamId;
    private String name;
    private String description;
    private TeamRole role;
}