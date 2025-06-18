package com.develop.logsentry.domain.log.entity;

public enum LogLevel {
    DEBUG(1), INFO(2), WARN(3), ERROR(4);

    private final int severity;

    LogLevel(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }

    public boolean isMoreSevereThan(LogLevel other) {
        return this.severity > other.severity;
    }
}