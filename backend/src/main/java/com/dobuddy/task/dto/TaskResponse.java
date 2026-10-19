package com.dobuddy.task.dto;

import com.dobuddy.dashboard.dto.GoalProgressResponse;
import com.dobuddy.dashboard.dto.WeeklyProgressResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private Integer taskId;
    private Integer goalId;
    private LocalDate taskDate;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isCompleted;
    private WeeklyProgressResponse weeklyProgress;
    private List<GoalProgressResponse> goalProgress;
}
