package com.dobuddy.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyScheduler {

    private final WeeklySnapshotService snapshotService;
    private final WeeklyAggregationService aggregationService;

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY = 1 * 60 * 1000L;

    // 월요일 자정에 실행 : 0 0 0 * * MON
    @Scheduled(cron = "0 50 0 * * SUN", zone = "Asia/Seoul")
    public void runWeeklyAnalytics() {

        LocalDate weekStart = LocalDate.now(ZoneId.of("Asia/Seoul"))
                //.minusWeeks(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 스냅샷 저장
        if (!snapshotService.isSnapshotSaved(weekStart)) {
            saveSnapshotWithRetry(weekStart);
        }

        aggregationService.weeklyAggregate(weekStart);
    }

    private void saveSnapshotWithRetry(LocalDate weekStart) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                snapshotService.saveSnapshot(weekStart);

                log.info("스냅샷 저장 성공: weekStart={}, attempt={}",
                        weekStart, attempt);
                return;

            } catch (Exception e) {
                log.error("스냅샷 저장 실패: weekStart={}, attempt={}/{}",
                        weekStart, attempt, MAX_ATTEMPTS, e);

                if (attempt == MAX_ATTEMPTS) {
                    snapshotService.saveFailure(
                            weekStart,
                            "SNAPSHOT_SAVE_FAILED: " + e.getMessage()
                    );

                    throw new IllegalStateException(
                            "스냅샷 저장 최종 실패: " + weekStart, e
                    );
                }

                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();

                    throw new IllegalStateException(
                            "스냅샷 재시도 중단", interruptedException
                    );
                }
            }
        }
    }


}
