package com.dobuddy.analytics;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(
        name = "weekly_user_status",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_weekly_user_status",
                columnNames = {"week_start", "user_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyUserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private boolean isPower;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public WeeklyUserStatus(
            LocalDate weekStart,
            Integer userId,
            boolean isPower
    ) {
        this.weekStart = weekStart;
        this.userId = userId;
        this.isPower = isPower;
        this.createdAt = LocalDateTime.now(
                ZoneId.of("Asia/Seoul")
        );
    }
}