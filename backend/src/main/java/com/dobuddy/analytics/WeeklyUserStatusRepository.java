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
            u.user_id AS "userId",
            (COALESCE(a.day_count, 0) >= 2) AS "isPower"
        FROM weekly_users wu
        JOIN weekly_snapshot_users u
            ON wu.user_id = u.user_id
            AND u.week_start = :weekStart
        LEFT JOIN achievement_days a
            ON u.user_id = a.user_id
        WHERE u.role <> 'ADMIN'
        """, nativeQuery = true)
    List<WeeklyPowerResult> calculateWeeklyPower(
            @Param("weekStart") LocalDate weekStart,
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
    JOIN weekly_snapshot_users u 
        ON u.user_id = l.user_id
        AND u.week_start = :weekStart
    WHERE l.rn = 1
      AND l.action = 'complete'
      AND l.occurred_at >= :weekStartTime
      AND u.role <> 'ADMIN'
    GROUP BY l.user_id
    """, nativeQuery = true)
    List<WeeklyAchievementDaysResult> calculateAchievementDays(
            @Param("weekStart") LocalDate weekStart,
            @Param("weekStartTime") LocalDateTime weekStartTime,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM weekly_user_status wus
    WHERE wus.week_start = :weekStart
      AND wus.is_power = true
      AND EXISTS (
          SELECT 1
          FROM weekly_snapshot_study_members sm
          WHERE sm.user_id = wus.user_id
            AND sm.week_start = :weekStart
      )
    """, nativeQuery = true)
    long countStudyJoinedPowerUsers(
            @Param("weekStart") LocalDate weekStart
    );

    void deleteByWeekStart(LocalDate weekStart);
}