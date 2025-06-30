package com.develop.logsentry.domain.alert.service;

import com.develop.logsentry.domain.alert.entity.TwilioSmsSender;
import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final TwilioSmsSender smsSender;
    private final String toNumber;

    public void sendLogErrorAlert(LogMessageDto dto) {
        String message = """
            🚨 [ERROR LOG 발생]
            예외: %s
            메시지: %s
            발생 시각: %s
            """.formatted(
                dto.getExceptionName(),
                dto.getMessage(),
                dto.getTimestamp()
        );

        smsSender.sendSms(toNumber, message);
    }
}