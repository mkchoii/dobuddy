package com.dobuddy.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DashboardTaskResponse {

    private Integer taskId;
    private String content;
    private boolean isCompleted;
    private LocalDateTime createdAt;
}
