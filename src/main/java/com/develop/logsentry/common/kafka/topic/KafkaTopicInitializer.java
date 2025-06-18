package com.develop.logsentry.common.kafka.topic;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTopicInitializer {
    private final KafkaTopicManager kafkaTopicManager;

    @PostConstruct
    public void init() {
        kafkaTopicManager.createTopicIfNotExists(KafkaTopic.LOG.get());
    }
}