package com.develop.logsentry.common.exception;

import com.develop.logsentry.domain.log.entity.LogLevel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Getter
public enum ErrorCode {
    // User
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, LogLevel.WARN, "%s는 이미 존재합니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, LogLevel.WARN, "중복된 Email입니다"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, LogLevel.WARN, "중복된 사용자가 존재합니다."),
    INVALID_ADMIN_KEY(HttpStatus.UNAUTHORIZED, LogLevel.WARN, "입력한 키가 관리자 키와 맞지않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.WARN, "사용자를 찾을 수 없습니다."),
    USER_INACTIVE(HttpStatus.UNAUTHORIZED, LogLevel.WARN, "사용자를 찾을 수 없거나 비활성화 상태입니다."),

    // Team
    TEAM_ALREADY_EXISTS(HttpStatus.CONFLICT, LogLevel.WARN, "%s는 이미 존재합니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.WARN, "팀을 찾을 수 없습니다."),
    TEAM_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, LogLevel.WARN, "팀에 대하여 접근할 수 없습니다."),
    NO_TEAM_ADMIN_PRIVILEGE(HttpStatus.UNAUTHORIZED, LogLevel.WARN, "사용자가 해당 팀의 권한이 ADMIN이 아닙니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, LogLevel.WARN, "이미 초대되었습니다."),
    USER_ALREADY_IN_TEAM(HttpStatus.CONFLICT, LogLevel.WARN, "이미 가입된 유저입니다."),

    // Project
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.WARN, "해당 프로젝트를 찾을 수 없습니다. id = %d"),
    NO_PROJECT_OWNER_PRIVILEGE(HttpStatus.FORBIDDEN, LogLevel.WARN, "프로젝트 접근 권한이 없습니다."),
    PROJECT_INACTIVE(HttpStatus.UNAUTHORIZED, LogLevel.WARN, "삭제된 프로젝트입니다."),

    // Log
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.WARN, "해당 로그를 찾을 수 없습니다. id = %d"),
    INVALID_API_KEY(HttpStatus.UNAUTHORIZED, LogLevel.ERROR, "유효하지 않은 API Key입니다."),
    INVALID_METADATA_FORMAT(HttpStatus.UNAUTHORIZED, LogLevel.ERROR, "유효하지 않은 Metadata 형식입니다."),

    // 기본 코드
    NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.WARN, "%s을(를) 찾지못했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, LogLevel.ERROR, "비밀번호가 올바르지 않습니다."),
    EMAIL_NOT_MATCH(HttpStatus.CONFLICT, LogLevel.WARN, "Email이 맞지 않습니다."),
    INVALID_INVITATION_TOKEN(HttpStatus.UNAUTHORIZED, LogLevel.ERROR, "토큰이 유효하지 않습니다.");

    private final HttpStatus status;
    private final LogLevel logLevel;
    private final String message;

    ErrorCode(HttpStatus httpStatus, LogLevel logLevel, String message) {
        this.status = httpStatus;
        this.logLevel = logLevel;
        this.message = message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }

    private static final Set<ErrorCode> INVITATION_ERRORS = Set.of(
            ALREADY_INVITED, EMAIL_NOT_MATCH, INVALID_INVITATION_TOKEN
    );

    public String getSource() {
        if (this.name().contains("USER") || this == INVALID_ADMIN_KEY || this == INVALID_CREDENTIALS) {
            return "user";
        } else if (this.name().contains("TEAM")) {
            return "team";
        } else if (this.name().contains("PROJECT")) {
            return "project";
        } else if (this.name().contains("LOG")) {
            return "log";
        } else if (INVITATION_ERRORS.contains(this)) {
            return "invitation";
        }
        return "global";
    }
}