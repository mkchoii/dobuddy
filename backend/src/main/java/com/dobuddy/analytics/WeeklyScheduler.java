package com.dobuddy.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class WeeklyScheduler {

    private final WeeklyAnalyticsService weeklyAnalyticsService;

    // 월요일 자정에 실행 : 0 0 0 * * MON
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")

    public void aggregateWeekly() {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate lastWeekStart = today
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .minusWeeks(1);
        weeklyAnalyticsService.calculateWeeklyUserStatus(lastWeekStart);
    }
}
