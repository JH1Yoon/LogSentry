package com.develop.logsentry.common.kafka.topic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaTopicManager {
    private final KafkaAdmin kafkaAdmin;

    public void createTopicIfNotExists(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(topicName)) {
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                log.info("Created Kafka topic: {}", topicName);
            }
        } catch (Exception e) {
            log.error("Failed to create topic {}: {}", topicName, e.getMessage());
        }
    }
}