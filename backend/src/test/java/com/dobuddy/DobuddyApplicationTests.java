package com.dobuddy;

import com.dobuddy.analytics.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DobuddyApplicationTests {

    @Autowired
    private WeeklyAnalyticsService analyticsService;

    @Autowired
    private WeeklyUserStatusRepository userStatusRepository;

    @Autowired
    private WeeklyMetricRepository metricRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void rollbackWhenAggregationFails() {

        LocalDate weekStart = LocalDate.of(2026, 10, 26);

        // 1. 기존 데이터 개수 확인
        long originalStatusCount =
                userStatusRepository.findByWeekStart(weekStart).size();

        long originalMetricCount =
                metricRepository.findByWeekStart(weekStart).size();

        assertTrue(originalStatusCount > 0);
        assertTrue(originalMetricCount > 0);

        // 2. 트랜잭션 내부에서 삭제 및 일부 재계산 후 예외 발생
        assertThrows(IllegalStateException.class, () -> {

            transactionTemplate.executeWithoutResult(status -> {

                // 기존 집계 결과 삭제
                metricRepository.deleteByWeekStart(weekStart);
                userStatusRepository.deleteByWeekStart(weekStart);

                // 일부 지표 계산
                analyticsService.calculateWeeklyUserStatus(weekStart);
                analyticsService.calculatePowerUsers(weekStart);

                // 의도적으로 예외 발생
                throw new IllegalStateException("강제 집계 실패");
            });
        });

        // 3. 롤백 후 데이터 개수 확인
        long afterStatusCount =
                userStatusRepository.findByWeekStart(weekStart).size();

        long afterMetricCount =
                metricRepository.findByWeekStart(weekStart).size();

        // 4. 기존 데이터 유지 확인
        assertEquals(originalStatusCount, afterStatusCount);
        assertEquals(originalMetricCount, afterMetricCount);

        System.out.println("롤백 테스트 성공");
        System.out.println("사용자 상태: "
                + originalStatusCount + " → " + afterStatusCount);
        System.out.println("주간 지표: "
                + originalMetricCount + " → " + afterMetricCount);
    }

}
