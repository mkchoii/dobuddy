package com.dobuddy.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeeklyAggregationService {

    private final WeeklyAnalyticsService analyticsService;
    private final WeeklyUserStatusRepository userStatusRepository;
    private final WeeklyMetricRepository metricRepository;

    @Transactional
    public void weeklyAggregate(LocalDate weekStart) {

        // 기존 집계 결과 삭제
        metricRepository.deleteByWeekStart(weekStart);
        userStatusRepository.deleteByWeekStart(weekStart);

        // 파워 유저 여부
        analyticsService.calculateWeeklyUserStatus(weekStart);

        // 파워 유저 수
        analyticsService.calculatePowerUsers(weekStart);

        // 달성일 분포
        analyticsService.calculateAchievementDays(weekStart);

        // 활성 사용자 달성률
        analyticsService.calculateActiveUserRate(weekStart);

        // 주간 태스크 달성률
        analyticsService.calculateProgressRate(weekStart);

        // 카테고리별 목표 달성률
        analyticsService.calculateGoalProgressByCategory(weekStart);

        // 스터디 연동별 목표 달성률
        analyticsService.calculateGoalProgressByLink(weekStart);

        // 스터디별 활성 멤버 비율
        analyticsService.calculateStudyActiveMemberRate(weekStart);
    }
}
