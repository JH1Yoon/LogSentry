package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.domain.log.dto.request.LogMessageDto;
import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.repository.LogRepository;
import com.develop.logsentry.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogConsumerTest {
    @InjectMocks
    private LogConsumer logConsumer;

    @Mock
    private LogRepository logRepository;

    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        logRepository = mock(LogRepository.class);
        projectRepository = mock(ProjectRepository.class);
        logConsumer = new LogConsumer(logRepository, projectRepository);
    }


    @Test
    @DisplayName("프로젝트가 존재할 때 로그를 저장")
    void consume_shouldSaveLog_whenProjectExists() {
        // Given
        LogMessageDto dto = LogMessageDto.builder()
                .logLevel(LogLevel.ERROR)
                .exceptionName("NullPointerException")
                .errorCodeMessage("ERR001")
                .message("Something went wrong")
                .stackSummary("com.example.NullPointerException: ...")
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0))
                .projectId(1L)
                .build();

        when(projectRepository.existsById(1L)).thenReturn(true);

        // When
        logConsumer.consume(dto);

        // Then
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository).save(captor.capture());

        Log savedLog = captor.getValue();
        assertThat(savedLog.getProjectIdLegacy()).isEqualTo(1L);
        assertThat(savedLog.getLogLevel()).isEqualTo(LogLevel.ERROR);
        assertThat(savedLog.getExceptionName()).isEqualTo("NullPointerException");
    }

    @Test
    @DisplayName("프로젝트가 존재하지 않을 때 projectId 없이 로그를 저장")
    void consume_shouldSaveLogWithNullProject_whenProjectDoesNotExist() {
        // Given
        LogMessageDto dto = LogMessageDto.builder()
                .logLevel(LogLevel.WARN)
                .exceptionName("IllegalArgumentException")
                .errorCodeMessage("ERR002")
                .message("Invalid input")
                .stackSummary("com.example.IllegalArgumentException: ...")
                .timestamp(LocalDateTime.of(2023, 1, 1, 13, 0))
                .projectId(999L)
                .build();

        when(projectRepository.existsById(999L)).thenReturn(false);

        // When
        logConsumer.consume(dto);

        // Then
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository).save(captor.capture());

        Log savedLog = captor.getValue();
        assertThat(savedLog.getProjectIdLegacy()).isNull();
    }
}