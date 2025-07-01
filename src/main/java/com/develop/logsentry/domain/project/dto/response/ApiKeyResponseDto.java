package com.develop.logsentry.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiKeyResponseDto {
    @Schema(description = "재발급된 API 키", example = "e0ab3df4-4d77-49e7-879a-3a1234567890")
    private String apiKey;

    public ApiKeyResponseDto(String apiKey) {
        this.apiKey = apiKey;
    }
}