package com.dobuddy.auth;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class PasswordResetCountLimiter {

    private static final int DAILY_LIMIT = 5;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ConcurrentHashMap<String, ResetCount> resetCounts =
            new ConcurrentHashMap<>();

    private record ResetCount(LocalDate date, int count) {}
    public record ResetAttemptResult(int attemptCount, boolean allowed) {}
    public ResetAttemptResult tryAcquire(String email) {
        LocalDate today = LocalDate.now(KST);

        ResetCount updated = resetCounts.compute(email, (key, previous) -> {
            if (previous == null || !previous.date().equals(today)) {
                return new ResetCount(today, 1);
            }
            return new ResetCount(today, previous.count() + 1);
        });

        return new ResetAttemptResult(
                updated.count(),
                updated.count() <= DAILY_LIMIT
        );
    }
}