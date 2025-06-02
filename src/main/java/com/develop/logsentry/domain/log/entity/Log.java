package com.develop.logsentry.domain.log.entity;

import com.develop.logsentry.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그 레벨: INFO, WARN, ERROR 등
    @Column(nullable = false)
    private String level;

    // 핵심 메시지
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // 로그 발생 시간
    private LocalDateTime loggedAt;

    // 메타 정보
    private String serviceName;
    private String serverIp;
    private String traceId; // 분산 트레이싱용

    // 프로젝트 연관관계 (로그는 특정 프로젝트에 속함)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @PrePersist
    public void onCreate() {
        if (this.loggedAt == null) {
            this.loggedAt = LocalDateTime.now();
        }
    }
}
