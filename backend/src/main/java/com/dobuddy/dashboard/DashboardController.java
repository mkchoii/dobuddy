package com.dobuddy.dashboard;

import com.dobuddy.dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) LocalDate date,
            Authentication authentication
    ) {
        Integer userId = (Integer) authentication.getPrincipal();

        if (date == null) {
            date = LocalDate.now(ZoneId.of("Asia/Seoul"));
        }

        DashboardResponse response = dashboardService.getDashboard(userId, date);

        return ResponseEntity.ok(response);
    }
}
