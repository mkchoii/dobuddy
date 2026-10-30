package com.dobuddy.analytics;

import com.dobuddy.analytics.dto.WeeklyPowerResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeeklyUserStatusRepository extends JpaRepository<WeeklyUserStatus, Integer> {

    List<WeeklyUserStatus> findByWeekStart(LocalDate weekStart);
    Optional<WeeklyUserStatus> findByWeekStartAndUserId(
            LocalDate weekStart,
            Integer userId
    );
    @Query(value = """
        WITH latest_log AS (
            SELECT
                task_id,
                user_id,
                action,
                occurred_at,
                ROW_NUMBER() OVER (
                    PARTITION BY task_id
                    ORDER BY occurred_at DESC, id DESC
                ) AS rn
            FROM task_completion_log
            WHERE occurred_at < :weekEnd
        ),
        achievement_days AS (
            SELECT
                user_id,
                COUNT(DISTINCT occurred_at::date) AS day_count
            FROM latest_log
            WHERE rn = 1
              AND action = 'complete'
              AND occurred_at >= :weekStartTime
            GROUP BY user_id
        )
        SELECT
            u.id AS "userId",
            (COALESCE(a.day_count, 0) >= 2) AS "isPower"
        FROM users u
        LEFT JOIN achievement_days a
            ON u.id = a.user_id
        """, nativeQuery = true)
    List<WeeklyPowerResult> calculateWeeklyPower(
            @Param("weekStartTime") LocalDateTime weekStartTime,
            @Param("weekEnd") LocalDateTime weekEnd
    );
}