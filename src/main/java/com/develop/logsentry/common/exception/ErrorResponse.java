package com.develop.logsentry.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드")
    private int code;

    @Schema(description = "에러 메시지")
    private String message;

    @Schema(description = "HTTP 상태명")
    private String status;

    public static ErrorResponse from(ErrorCode errorCode, Object... args) {
        return of(
                errorCode.getStatus().value(),
                errorCode.getFormattedMessage(args),
                errorCode.getStatus().name()
        );
    }
}