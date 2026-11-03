package com.dobuddy.analytics;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(
        name = "weekly_metrics",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_weekly_metrics",
                columnNames = {
                        "week_start",
                        "metric_name",
                        "segment"
                }
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(nullable = false, length = 50)
    private String metricName;

    @Column(nullable = false, length = 30)
    private String segment = "all";

    @Column
    private Integer numerator;

    @Column
    private Integer denominator;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal value;

    @Column(nullable = false, length = 10)
    private String definitionVersion = "v1";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public WeeklyMetric(
            LocalDate weekStart,
            String metricName,
            String segment,
            Integer numerator,
            Integer denominator,
            BigDecimal value,
            String definitionVersion
    ) {
        this.weekStart = weekStart;
        this.metricName = metricName;
        this.segment = segment;
        this.numerator = numerator;
        this.denominator = denominator;
        this.value = value;
        this.definitionVersion = definitionVersion;
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
