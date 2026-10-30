package com.dobuddy.analytics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyMetricRepository extends JpaRepository<WeeklyMetric, Integer> {

    List<WeeklyMetric> findByWeekStart(LocalDate weekStart);
    Optional<WeeklyMetric> findByWeekStartAndMetricNameAndSegment(
            LocalDate weekStart,
            String metricName,
            String segment
    );
}
