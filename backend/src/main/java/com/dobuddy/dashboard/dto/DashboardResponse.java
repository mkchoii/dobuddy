package com.dobuddy.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private String finalGoal;
    private String nickname;
    private LocalDate selectedDate;
    private WeeklyProgressResponse weeklyProgress;
    private List<GoalProgressResponse> goalProgress;
    private List<DashboardGoalResponse> goals;
}
