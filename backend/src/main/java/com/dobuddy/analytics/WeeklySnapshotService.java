package com.dobuddy.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeeklySnapshotService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveSnapshot(LocalDate weekStart) {

        // 이미 성공한 스냅샷은 다시 생성 X
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                                SELECT 1
                                FROM weekly_snapshot_runs
                                WHERE week_start = ?
                                  AND status = 'SUCCESS'
                            )
                """, Boolean.class, weekStart);

        if (Boolean.TRUE.equals(exists)) {
            throw new IllegalStateException(
                    "이미 저장된 스냅샷입니다: " + weekStart
            );
        }

        // 1. 사용자
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_users
                            (week_start, user_id, access_at, role)
                        SELECT ?, id, access_at, role::text
                        FROM users
            """, weekStart);

        // 2. 스터디 멤버
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_study_members
                (week_start, study_member_id, study_id, user_id, joined_at)
            SELECT ?, id, study_id, user_id, joined_at
            FROM study_members
            """, weekStart);

        // 3. 스터디 목표 연동
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_study_member_goals
                (week_start, study_member_goal_id,
                 study_member_id, goal_id, created_at)
            SELECT ?, id, study_member_id, goal_id, created_at
            FROM study_member_goals
            """, weekStart);

        // 4. 목표
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_goals
                (week_start, goal_id, user_id, category)
            SELECT ?, id, user_id, category::text
            FROM goals
            """, weekStart);

        // 5. 해당 주의 태스크
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_tasks
                (week_start, task_id, goal_id, task_date)
            SELECT ?, id, goal_id, task_date
            FROM tasks
            WHERE task_date >= ?
              AND task_date < ?
            """,
                weekStart,
                weekStart,
                weekStart.plusWeeks(1));

        // 6. 성공 상태 저장
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_runs
                (week_start, status, captured_at, error_message)
            VALUES (?, 'SUCCESS', CURRENT_TIMESTAMP, NULL)
            ON CONFLICT (week_start)
            DO UPDATE SET
                status = 'SUCCESS',
                captured_at = EXCLUDED.captured_at,
                error_message = NULL
            """, weekStart);
    }
    @Transactional
    public void saveFailure(LocalDate weekStart, String errorMessage) {
        jdbcTemplate.update("""
            INSERT INTO weekly_snapshot_runs
                            (week_start, status, error_message)
                        VALUES (?, 'FAILED', ?)
                        ON CONFLICT (week_start)
                        DO UPDATE SET
                            status = 'FAILED',
                            error_message = EXCLUDED.error_message
                        WHERE weekly_snapshot_runs.status <> 'SUCCESS'
            """, weekStart, errorMessage);
    }

    public boolean isSnapshotSaved(LocalDate weekStart) {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                            SELECT 1
                            FROM weekly_snapshot_runs
                            WHERE week_start = ?
                              AND status = 'SUCCESS'
                        )
                """, Boolean.class, weekStart);

        return Boolean.TRUE.equals(exists);
    }
}
