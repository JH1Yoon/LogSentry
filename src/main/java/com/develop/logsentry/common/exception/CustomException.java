package com.develop.logsentry.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;
    private final Long projectId;

    public CustomException(ErrorCode errorCode, Long projectId, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.errorCode = errorCode;
        this.args = args;
        this.projectId = projectId;
    }

    public String getSource() {
        return errorCode.getSource();
    }
}