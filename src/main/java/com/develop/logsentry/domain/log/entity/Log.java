package com.develop.logsentry.domain.log.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    private String exceptionName;     // ex: CustomException

    private String errorCodeMessage;  // ex: USER_NOT_FOUND: 사용자 정보를 찾을 수 없습니다.

    @Column(columnDefinition = "TEXT")
    private String message;           // ex: 해당 아이디의 사용자가 존재하지 않습니다.

    @Column(columnDefinition = "TEXT")
    private String stackSummary;      // ex: CustomException at com.example.user.UserService.findUser(UserService.java:57)

    private LocalDateTime timestamp;  // ex: 2025-06-18T15:30:00
}