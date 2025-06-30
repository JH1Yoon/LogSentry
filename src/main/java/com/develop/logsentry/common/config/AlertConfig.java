package com.develop.logsentry.common.config;

import com.develop.logsentry.domain.alert.entity.TwilioSmsSender;
import com.develop.logsentry.domain.alert.service.AlertService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertConfig {

    @Value("${alert.phone-number}")
    private String toNumber;

    @Bean
    public AlertService alertService(TwilioSmsSender smsSender) {
        return new AlertService(smsSender, toNumber);
    }
}