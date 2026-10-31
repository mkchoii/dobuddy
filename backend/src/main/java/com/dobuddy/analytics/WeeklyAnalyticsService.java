package com.dobuddy.analytics;

import com.dobuddy.analytics.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyAnalyticsService {

    private final WeeklyUserStatusRepository weeklyUserStatusRepository;
    private final WeeklyMetricRepository weeklyMetricRepository;

    @Transactional
    public void calculateWeeklyUserStatus(LocalDate weekStart) {

        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1).atStartOfDay();

        // 사용자별 파워 유저 여부
        List<WeeklyPowerResult> results = weeklyUserStatusRepository.calculateWeeklyPower(
                weekStart,
                weekStartTime,
                weekEnd
        );

        List<WeeklyUserStatus> statuses = results.stream()
                .map(result -> new WeeklyUserStatus(
                        weekStart,
                        result.getUserId(),
                        result.getIsPower()
                ))
                .toList();

        weeklyUserStatusRepository.saveAll(statuses);
    }

    @Transactional
    public void calculatePowerUsers(LocalDate weekStart) {

        // 전체 파워 유저 수
        long allCount = weeklyUserStatusRepository
                .findByWeekStartAndIsPowerTrue(weekStart)
                .size();

        // 스터디 가입 파워 유저 수
        long joinedCount = weeklyUserStatusRepository
                .countStudyJoinedPowerUsers(weekStart);

        // 스터디 미가입 파워 유저 수
        long notJoinedCount = allCount - joinedCount;

        // 세그먼트별 지표 생성
        List<WeeklyMetric> metrics = List.of(
                createPowerUsersMetric(weekStart, "all", allCount),
                createPowerUsersMetric(weekStart, "study_joined", joinedCount),
                createPowerUsersMetric(weekStart, "study_not_joined", notJoinedCount)
        );

        weeklyMetricRepository.saveAll(metrics);
    }

    private WeeklyMetric createPowerUsersMetric(LocalDate weekStart, String segment, long count) {
        return new WeeklyMetric(
                weekStart,
                "power_users",
                segment,
                null,
                null,
                BigDecimal.valueOf(count),
                "v1"
        );
    }

    @Transactional
    public void calculateAchievementDays(LocalDate weekStart) {

        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1).atStartOfDay();

        // 사용자별 달성일 수 조회
        List<WeeklyAchievementDaysResult> results = weeklyUserStatusRepository.calculateAchievementDays(
                weekStart,
                weekStartTime,
                weekEnd
        );

        // 달성일 1일 이상인 전체 사용자 수
        long denominator = results.size();

        // 구간별 사용자 수
        long oneDay = results.stream()
                .filter(r -> r.getDayCount() == 1)
                .count();

        long twoDays = results.stream()
                .filter(r -> r.getDayCount() == 2)
                .count();

        long threeDays = results.stream()
                .filter(r -> r.getDayCount() == 3)
                .count();

        long fourPlus = results.stream()
                .filter(r -> r.getDayCount() >= 4)
                .count();

        // 세그먼트별 지표 생성
        List<WeeklyMetric> metrics = List.of(
                createAchievementDaysMetric(weekStart, "1day", oneDay, denominator),
                createAchievementDaysMetric(weekStart, "2days", twoDays, denominator),
                createAchievementDaysMetric(weekStart, "3days", threeDays, denominator),
                createAchievementDaysMetric(weekStart, "4plus", fourPlus, denominator)
        );

        weeklyMetricRepository.saveAll(metrics);
    }

    private WeeklyMetric createAchievementDaysMetric(
            LocalDate weekStart,
            String segment,
            long numerator,
            long denominator
    ) {
        BigDecimal value = denominator == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);

        return new WeeklyMetric(
                weekStart,
                "achievement_days",
                segment,
                Math.toIntExact(numerator),
                Math.toIntExact(denominator),
                value,
                "v1"
        );
    }

    @Transactional
    public void calculateActiveUserRate(LocalDate weekStart) {

        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1).atStartOfDay();

        LocalDate activeSince = weekEnd.toLocalDate().minusDays(14);

        // 접속 유저 수 및 달성 유저 수 집계
        ActiveUserRateResult result = weeklyMetricRepository.calculateActiveUserRate(
                weekStart,
                weekStartTime,
                weekEnd,
                activeSince
        );

        int numerator = Math.toIntExact(result.getNumerator());
        int denominator = Math.toIntExact(result.getDenominator());

        BigDecimal value = denominator == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(numerator).divide(
                        BigDecimal.valueOf(denominator),
                4,
                RoundingMode.HALF_UP
        );

        WeeklyMetric metric = new WeeklyMetric(
                weekStart,
                "active_user_rate",
                "all",
                numerator,
                denominator,
                value,
                "v1"
        );

        weeklyMetricRepository.save(metric);
    }

    @Transactional
    public void calculateProgressRate(LocalDate weekStart) {

        LocalDate weekEndDate = weekStart.plusWeeks(1);
        LocalDateTime weekEnd = weekEndDate.atStartOfDay();

        // 사용자별 예정, 완료 태스크 수 및 스터디 가입 여부
        List<WeeklyProgressResult> results = weeklyMetricRepository.calculateWeeklyProgress(
                weekStart,
                weekEndDate,
                weekEnd
        );

        // 세그먼트별 분류
        List<WeeklyProgressResult> joined = results.stream()
                .filter(WeeklyProgressResult::getStudyJoined)
                .toList();

        List<WeeklyProgressResult> notJoined = results.stream()
                .filter(r -> !r.getStudyJoined())
                .toList();

        List<WeeklyMetric> metrics = List.of(
                createAverageProgressMetric(
                        weekStart,
                        "progress_rate",
                        "all",
                        results
                ),
                createAverageProgressMetric(
                        weekStart,
                        "progress_rate",
                        "study_joined",
                        joined
                ),
                createAverageProgressMetric(
                        weekStart,
                        "progress_rate",
                        "study_not_joined",
                        notJoined
                )
        );

        weeklyMetricRepository.saveAll(metrics);
    }

    @Transactional
    public void calculateGoalProgressByCategory(LocalDate weekStart) {

        LocalDate weekEndDate = weekStart.plusWeeks(1);
        LocalDateTime weekEnd = weekEndDate.atStartOfDay();

        List<GoalProgressResult> results =
                weeklyMetricRepository.calculateGoalProgressByCategory(
                        weekStart,
                        weekEndDate,
                        weekEnd
                );

        // 카테고리별로 목표 분류
        Map<String, List<GoalProgressResult>> grouped =
                results.stream()
                        .collect(Collectors.groupingBy(
                                GoalProgressResult::getCategory
                        ));

        List<WeeklyMetric> metrics = new ArrayList<>();

        for (Map.Entry<String, List<GoalProgressResult>> entry
                : grouped.entrySet()) {

            metrics.add(createAverageProgressMetric(
                    weekStart,
                    "goal_progress_by_category",
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        weeklyMetricRepository.saveAll(metrics);
    }

    @Transactional
    public void calculateGoalProgressByLink(LocalDate weekStart) {

        LocalDate weekEndDate = weekStart.plusWeeks(1);
        LocalDateTime weekEnd = weekEndDate.atStartOfDay();

        List<GoalProgressByLinkResult> results =
                weeklyMetricRepository.calculateGoalProgressByLink(
                        weekStart,
                        weekEndDate,
                        weekEnd
                );

        List<GoalProgressByLinkResult> linked = results.stream()
                .filter(GoalProgressByLinkResult::getLinked)
                .toList();

        List<GoalProgressByLinkResult> notLinked = results.stream()
                .filter(r -> !r.getLinked())
                .toList();

        List<WeeklyMetric> metrics = List.of(
                createAverageProgressMetric(
                        weekStart,
                        "goal_progress_by_link",
                        "linked",
                        linked
                ),
                createAverageProgressMetric(
                        weekStart,
                        "goal_progress_by_link",
                        "not_linked",
                        notLinked
                )
        );

        weeklyMetricRepository.saveAll(metrics);
    }

    private WeeklyMetric createAverageProgressMetric(
            LocalDate weekStart,
            String metricName,
            String segment,
            List<? extends ProgressCountResult> items
    ) {
        int denominator = items.size();

        BigDecimal sum = items.stream()
                .map(item -> BigDecimal.valueOf(item.getCompletedCount())
                        .divide(
                                BigDecimal.valueOf(item.getPlannedCount()),
                                12,
                                RoundingMode.HALF_UP
                        ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal value = denominator == 0
                ? BigDecimal.ZERO
                : sum.divide(
                BigDecimal.valueOf(denominator),
                4,
                RoundingMode.HALF_UP
        );

        return new WeeklyMetric(
                weekStart,
                metricName,
                segment,
                null,
                denominator,
                value,
                "v1"
        );
    }

    @Transactional
    public void calculateStudyActiveMemberRate(LocalDate weekStart) {

        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1).atStartOfDay();

        List<StudyActiveMemberResult> results =
                weeklyMetricRepository.calculateStudyActiveMemberRate(
                        weekStart,
                        weekStartTime,
                        weekEnd
                );

        List<WeeklyMetric> metrics = results.stream()
                .map(result -> {
                    int numerator = Math.toIntExact(result.getNumerator());
                    int denominator = Math.toIntExact(result.getDenominator());

                    BigDecimal value = BigDecimal.valueOf(numerator)
                            .divide(
                                    BigDecimal.valueOf(denominator),
                                    4,
                                    RoundingMode.HALF_UP
                            );

                    return new WeeklyMetric(
                            weekStart,
                            "study_active_member_rate",
                            "study_" + result.getStudyId(),
                            numerator,
                            denominator,
                            value,
                            "v1"
                    );
                })
                .toList();

        weeklyMetricRepository.saveAll(metrics);
    }

}
