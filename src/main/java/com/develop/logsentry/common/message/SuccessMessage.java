package com.develop.logsentry.common.message;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessMessage {
    // Invitation
    INVITATION_SEND_SUCCESS(HttpStatus.OK, "초대가 성공적으로 전송되었습니다."),
    INVITATION_ACEEPT_SUCCESS(HttpStatus.OK, "초대를 수락했습니다."),

    // 기본 코드
    POSTED(HttpStatus.CREATED, "%s을(를) 등록했습니다."),
    CREATED(HttpStatus.CREATED, "%s을(를) 생성했습니다."),
    MODIFIED(HttpStatus.OK, "%s을(를) 수정했습니다."),
    DELETED(HttpStatus.OK, "%s을(를) 삭제했습니다.");

    private final HttpStatus status;
    private final String message;

    SuccessMessage(HttpStatus httpStatus, String message){
        this.status = httpStatus;
        this.message = message;
    }

    public String getMessage(String detail) {
        return String.format(message, detail);
    }

    public String getMessage(String detail1, String detail2) {
        return String.format(message, detail1, detail2);
    }
}