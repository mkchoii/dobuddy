package com.dobuddy.analytics;

import com.dobuddy.analytics.dto.WeeklyAchievementDaysResult;
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
        WITH weekly_users AS (
            SELECT DISTINCT user_id
            FROM task_completion_log
            WHERE occurred_at >= :weekStartTime
                AND occurred_at < :weekEnd
        ),
        latest_log AS (
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
        FROM weekly_users wu
        JOIN users u
            ON wu.user_id = u.id
        LEFT JOIN achievement_days a
            ON u.id = a.user_id
        WHERE LOWER(CAST(u.role AS TEXT)) <> 'admin'
        """, nativeQuery = true)
    List<WeeklyPowerResult> calculateWeeklyPower(
            @Param("weekStartTime") LocalDateTime weekStartTime,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    List<WeeklyUserStatus> findByWeekStartAndIsPowerTrue(LocalDate weekStart);

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
    )
    SELECT
        l.user_id AS "userId",
        COUNT(DISTINCT l.occurred_at::date) AS "dayCount"
    FROM latest_log l
    JOIN users u ON u.id = l.user_id
    WHERE l.rn = 1
      AND l.action = 'complete'
      AND l.occurred_at >= :weekStartTime
      AND LOWER(CAST(u.role AS TEXT)) <> 'admin'
    GROUP BY l.user_id
    """, nativeQuery = true)
    List<WeeklyAchievementDaysResult> calculateAchievementDays(
            @Param("weekStartTime") LocalDateTime weekStartTime,
            @Param("weekEnd") LocalDateTime weekEnd
    );
}