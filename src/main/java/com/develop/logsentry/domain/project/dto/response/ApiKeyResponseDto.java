package com.develop.logsentry.domain.project.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiKeyResponseDto {
    private String apiKey;

    public ApiKeyResponseDto(String apiKey) {
        this.apiKey = apiKey;
    }
}
