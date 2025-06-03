package com.develop.logsentry.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // User
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "%s는 이미 존재합니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "중복된 Email입니다"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "중복된 사용자가 존재합니다."),
    INVALID_ADMIN_KEY(HttpStatus.UNAUTHORIZED, "입력한 키가 관리자 키와 맞지않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_INACTIVE(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없거나 비활성화 상태입니다."),

    // 기본 코드
    NOT_FOUND(HttpStatus.NOT_FOUND, "%s을(를) 찾지못했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;

    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }

    ErrorCode(HttpStatus httpStatus, String message){
        this.status = httpStatus;
        this.message = message;
    }
}
