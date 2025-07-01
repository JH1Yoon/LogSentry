package com.develop.logsentry.domain.team.dto.response;

import com.develop.logsentry.domain.team.entity.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamSummaryResponseDto {
    @Schema(description = "팀 ID", example = "1")
    private Long teamId;

    @Schema(description = "팀 이름", example = "Dev Team")
    private String name;

    @Schema(description = "팀 설명", example = "서비스 백엔드 개발팀")
    private String description;

    @Schema(description = "내 역할", example = "ADMIN")
    private TeamRole role;
}