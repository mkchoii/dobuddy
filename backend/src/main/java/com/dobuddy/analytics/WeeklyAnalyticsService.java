package com.dobuddy.analytics;

import com.dobuddy.analytics.dto.WeeklyPowerResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyAnalyticsService {

    private final WeeklyUserStatusRepository weeklyUserStatusRepository;

    @Transactional
    public void calculateWeeklyUserStatus(LocalDate weekStart) {

        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1).atStartOfDay();

        // 사용자별 파워 유저 여부
        List<WeeklyPowerResult> results = weeklyUserStatusRepository.calculateWeeklyPower(
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
}
