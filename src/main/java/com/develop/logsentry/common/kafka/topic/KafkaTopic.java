package com.develop.logsentry.common.kafka.topic;

public enum KafkaTopic {
    LOG("logs-topic");

    private final String topic;
    public String get() { return topic; }

    KafkaTopic(String topic) { this.topic = topic; }
}