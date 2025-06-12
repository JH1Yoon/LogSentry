package com.develop.logsentry.domain.project.entity;

import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.team.entity.Team;
import com.develop.logsentry.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 프로젝트 이름 (팀 내에서 유일해야 함)
    @Column(nullable = false)
    private String name;

    // API Key는 외부에서 사용하므로 유일성 필요
    @Column(nullable = false, unique = true, length = 64)
    private String apiKey;

    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 연관관계: 프로젝트는 한 팀에 속한다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 향후 로그 데이터와의 연관관계 (1:N)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Log> logs = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column
    private LocalDateTime deletedAt;

    public String regenerateApiKey() {
        this.apiKey = UUID.randomUUID().toString();
        return this.apiKey;
    }

    public void update(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null && !description.trim().isEmpty()) {
            this.description = description;
        }
    }

    public void deactivate() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }
}
