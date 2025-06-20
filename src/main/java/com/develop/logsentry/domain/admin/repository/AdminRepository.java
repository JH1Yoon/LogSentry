package com.develop.logsentry.domain.admin.repository;

import com.develop.logsentry.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.isActive = true")
    Long countActiveProjects();

    @Query("SELECT COUNT(l) FROM Log l")
    Long countAllLogs();

    @Query("SELECT DATE(u.createdAt), COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end GROUP BY DATE(u.createdAt)")
    List<Object[]> countDailyActiveUsers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('DATE_FORMAT', p.createdAt, '%Y-%m'), COUNT(p) FROM Project p WHERE p.createdAt BETWEEN :start AND :end GROUP BY FUNCTION('DATE_FORMAT', p.createdAt, '%Y-%m')")
    List<Object[]> countMonthlyProjectActivity(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
