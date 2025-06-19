package com.develop.logsentry.common.exception;

import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.service.LogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final LogProducer logProducer;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        Long projectId = ex.getProjectId();
        sendLogToKafka(projectId, ex.getErrorCode().getLogLevel(), ex, ex.getSource(), ex.getErrorCode().name());

        return new ResponseEntity<>(ErrorResponse.from(ex.getErrorCode(), ex.getArgs()), ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        sendLogToKafka(null, LogLevel.ERROR, ex, "global", "GLOBAL_EXCEPTION");

        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        sendLogToKafka(null, LogLevel.WARN, ex, "validation", "VALIDATION_ERROR");
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        return getErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        sendLogToKafka(null, LogLevel.WARN, ex, "security", "ACCESS_DENIED"
        );
        return getErrorResponse(HttpStatus.FORBIDDEN, "접근이 거부되었습니다.");
    }

    private void sendLogToKafka(Long projectId, LogLevel logLevel, Exception ex, String source, String errorCategory) {
        String exceptionName = ex.getClass().getSimpleName();
        String errorCodeMessage;

        if (ex instanceof CustomException customEx) {
            errorCodeMessage = customEx.getErrorCode().name() + ": " + customEx.getErrorCode().getMessage();
        } else {
            errorCodeMessage = exceptionName;
        }

        String message = ex.getMessage();
        String stackSummary = getStackTraceSummary(ex);

        logProducer.sendLog(
                projectId,
                logLevel,
                exceptionName,
                errorCodeMessage,
                message,
                stackSummary,
                source,
                errorCategory
        );
    }

    private String getStackTraceSummary(Exception ex) {
        StackTraceElement[] elements = ex.getStackTrace();
        if (elements.length == 0) return "";
        return ex.getClass().getSimpleName() + " at " + elements[0].toString();
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(ErrorCode errorCode) {
        return new ResponseEntity<>(ErrorResponse.from(errorCode), errorCode.getStatus());
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = ErrorResponse.of(
                status.value(),
                message,
                status.name()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}