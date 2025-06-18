package com.develop.logsentry.domain.log.service;

import com.develop.logsentry.domain.log.dto.response.LogResponseDto;
import com.develop.logsentry.domain.log.dto.response.LogStatisticsDto;
import com.develop.logsentry.domain.log.entity.Log;
import com.develop.logsentry.domain.log.entity.LogLevel;
import com.develop.logsentry.domain.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

    private final LogRepository logRepository;

    /** 로그 조건 검색
     *
     * @param logLevel
     * @param start
     * @param end
     * @param page
     * @param size
     * @return Page<LogResponseDto>
     */
    public Page<LogResponseDto> getLogs(LogLevel logLevel, LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        Page<Log> logs;
        if (logLevel != null) {
            logs = logRepository.findByLogLevelAndTimestampBetween(logLevel, start, end, pageable);
        } else {
            logs = logRepository.findByTimestampBetween(start, end, pageable);
        }

        return logs.map(log -> new LogResponseDto(
                log.getId(),
                log.getLogLevel(),
                log.getExceptionName(),
                log.getErrorCodeMessage(),
                log.getMessage(),
                log.getStackSummary(),
                log.getTimestamp()
        ));
    }

    /** 로그 상세 조회
     *
     * @param id
     * @return LogResponseDto
     */
    public LogResponseDto getLogDetail(Long id) {
        Log log = logRepository.findByIdOrThrow(id);

        return new LogResponseDto(
                log.getId(),
                log.getLogLevel(),
                log.getExceptionName(),
                log.getErrorCodeMessage(),
                log.getMessage(),
                log.getStackSummary(),
                log.getTimestamp()
        );
    }

    /** 로그 통계 조회
     *
     * @param start
     * @param end
     * @return List<LogStatisticsDto>
     */
    public List<LogStatisticsDto> getLogStatistics(LocalDateTime start, LocalDateTime end) {
        return logRepository.countGroupByLogLevel(start, end)
                .stream()
                .map(record -> new LogStatisticsDto((LogLevel) record[0], (Long) record[1]))
                .toList();
    }
}
