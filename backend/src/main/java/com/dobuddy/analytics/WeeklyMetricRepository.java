package com.dobuddy.analytics;

import com.dobuddy.analytics.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeeklyMetricRepository extends JpaRepository<WeeklyMetric, Integer> {

    List<WeeklyMetric> findByWeekStart(LocalDate weekStart);
    Optional<WeeklyMetric> findByWeekStartAndMetricNameAndSegment(
            LocalDate weekStart,
            String metricName,
            String segment
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
    achieved_users AS (
        SELECT DISTINCT user_id
        FROM latest_log
        WHERE rn = 1
          AND action = 'complete'
          AND occurred_at >= :weekStartTime
    )
    SELECT
        COUNT(a.user_id) AS "numerator",
        COUNT(*) AS "denominator"
    FROM users u
    LEFT JOIN achieved_users a
        ON u.id = a.user_id
    WHERE LOWER(CAST(u.role AS TEXT)) <> 'admin'
      AND u.access_at IS NOT NULL
      AND u.access_at::date >= :activeSince
      AND u.access_at < :weekEnd
    """, nativeQuery = true)
    ActiveUserRateResult calculateActiveUserRate(
            @Param("weekStartTime") LocalDateTime weekStartTime,
            @Param("weekEnd") LocalDateTime weekEnd,
            @Param("activeSince") LocalDate activeSince
    );

    @Query(value = """
    WITH latest_log AS (
        SELECT
            task_id,
            user_id,
            task_date,
            action,
            ROW_NUMBER() OVER (
                PARTITION BY task_id
                ORDER BY occurred_at DESC, id DESC
            ) AS rn
        FROM task_completion_log
        WHERE occurred_at < :weekEnd
    ),
    last_status AS (
        SELECT
            task_id,
            user_id,
            task_date,
            action
        FROM latest_log
        WHERE rn = 1
    ),
    planned_tasks AS (
        -- 1. 현재 tasks에 존재하는 예정 태스크
        SELECT
            t.id AS task_id,
            g.user_id,
            CASE
                WHEN l.action = 'complete' THEN 1
                ELSE 0
            END AS is_completed
        FROM tasks t
        JOIN goals g ON g.id = t.goal_id
        LEFT JOIN last_status l ON l.task_id = t.id
        WHERE t.task_date >= :weekStart
          AND t.task_date < :weekEndDate

        UNION ALL

        -- 2. 삭제됐지만 기준 시점에 완료된 예정 태스크
        SELECT
            l.task_id,
            l.user_id,
            1 AS is_completed
        FROM last_status l
        WHERE l.action = 'complete'
          AND l.task_date >= :weekStart
          AND l.task_date < :weekEndDate
          AND NOT EXISTS (
              SELECT 1
              FROM tasks t
              WHERE t.id = l.task_id
          )
    )
    SELECT
        p.user_id AS "userId",
        COUNT(*) AS "plannedCount",
        SUM(p.is_completed) AS "completedCount",
        EXISTS (
            SELECT 1
            FROM study_members sm
            WHERE sm.user_id = p.user_id
        ) AS "studyJoined"
    FROM planned_tasks p
    JOIN users u ON u.id = p.user_id
    WHERE LOWER(CAST(u.role AS TEXT)) <> 'admin'
    GROUP BY p.user_id
    """, nativeQuery = true)
    List<WeeklyProgressResult> calculateWeeklyProgress(
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEndDate") LocalDate weekEndDate,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    @Query(value = """
    WITH latest_log AS (
        SELECT
            task_id,
            goal_id,
            task_date,
            action,
            ROW_NUMBER() OVER (
                PARTITION BY task_id
                ORDER BY occurred_at DESC, id DESC
            ) AS rn
        FROM task_completion_log
        WHERE occurred_at < :weekEnd
    ),
    last_status AS (
        SELECT
            task_id,
            goal_id,
            task_date,
            action
        FROM latest_log
        WHERE rn = 1
    ),
    planned_tasks AS (
        -- 1. 현재 존재하는 예정 태스크
        SELECT
            t.id AS task_id,
            t.goal_id,
            CASE
                WHEN l.action = 'complete' THEN 1
                ELSE 0
            END AS is_completed
        FROM tasks t
        LEFT JOIN last_status l ON l.task_id = t.id
        WHERE t.task_date >= :weekStart
          AND t.task_date < :weekEndDate

        UNION ALL

        -- 2. 완료 후 삭제된 예정 태스크
        SELECT
            l.task_id,
            l.goal_id,
            1 AS is_completed
        FROM last_status l
        WHERE l.action = 'complete'
          AND l.task_date >= :weekStart
          AND l.task_date < :weekEndDate
          AND NOT EXISTS (
              SELECT 1
              FROM tasks t
              WHERE t.id = l.task_id
          )
    )
    SELECT
        g.id AS "goalId",
        CAST(g.category AS TEXT) AS "category",
        COUNT(*) AS "plannedCount",
        SUM(p.is_completed) AS "completedCount"
    FROM planned_tasks p
    JOIN goals g ON g.id = p.goal_id
    JOIN users u ON u.id = g.user_id
    WHERE LOWER(CAST(u.role AS TEXT)) <> 'admin'
    GROUP BY g.id, g.category
    """, nativeQuery = true)
    List<GoalProgressResult> calculateGoalProgressByCategory(
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEndDate") LocalDate weekEndDate,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    @Query(value = """
    WITH latest_log AS (
        SELECT
            task_id,
            goal_id,
            task_date,
            action,
            ROW_NUMBER() OVER (
                PARTITION BY task_id
                ORDER BY occurred_at DESC, id DESC
            ) AS rn
        FROM task_completion_log
        WHERE occurred_at < :weekEnd
    ),
    last_status AS (
        SELECT
            task_id,
            goal_id,
            task_date,
            action
        FROM latest_log
        WHERE rn = 1
    ),
    planned_tasks AS (
        -- 1. 현재 존재하는 예정 태스크
        SELECT
            t.id AS task_id,
            t.goal_id,
            CASE
                WHEN l.action = 'complete' THEN 1
                ELSE 0
            END AS is_completed
        FROM tasks t
        LEFT JOIN last_status l ON l.task_id = t.id
        WHERE t.task_date >= :weekStart
          AND t.task_date < :weekEndDate

        UNION ALL

        -- 2. 완료 후 삭제된 예정 태스크
        SELECT
            l.task_id,
            l.goal_id,
            1 AS is_completed
        FROM last_status l
        WHERE l.action = 'complete'
          AND l.task_date >= :weekStart
          AND l.task_date < :weekEndDate
          AND NOT EXISTS (
              SELECT 1
              FROM tasks t
              WHERE t.id = l.task_id
          )
    )
    SELECT
        g.id AS "goalId",
        COUNT(*) AS "plannedCount",
        SUM(p.is_completed) AS "completedCount",
        EXISTS (
            SELECT 1
            FROM study_member_goals smg
            WHERE smg.goal_id = g.id
        ) AS "linked"
    FROM planned_tasks p
    JOIN goals g ON g.id = p.goal_id
    JOIN users u ON u.id = g.user_id
    WHERE LOWER(CAST(u.role AS TEXT)) <> 'admin'
      AND EXISTS (
          SELECT 1
          FROM study_members sm
          WHERE sm.user_id = u.id
      )
    GROUP BY g.id
    """, nativeQuery = true)
    List<GoalProgressByLinkResult> calculateGoalProgressByLink(
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEndDate") LocalDate weekEndDate,
            @Param("weekEnd") LocalDateTime weekEnd
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
    achieved_users AS (
        SELECT DISTINCT user_id
        FROM latest_log
        WHERE rn = 1
          AND action = 'complete'
          AND occurred_at >= :weekStartTime
    )
    SELECT
        sm.study_id AS "studyId",
        COUNT(a.user_id) AS "numerator",
        COUNT(*) AS "denominator"
    FROM study_members sm
    JOIN users u ON u.id = sm.user_id
    LEFT JOIN achieved_users a ON a.user_id = sm.user_id
    WHERE LOWER(CAST(u.role AS TEXT)) <> 'admin'
    GROUP BY sm.study_id
    """, nativeQuery = true)
    List<StudyActiveMemberResult> calculateStudyActiveMemberRate(
            @Param("weekStartTime") LocalDateTime weekStartTime,
            @Param("weekEnd") LocalDateTime weekEnd
    );
}
