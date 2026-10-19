package com.dobuddy.task.dto;

import com.dobuddy.dashboard.dto.GoalProgressResponse;
import com.dobuddy.dashboard.dto.WeeklyProgressResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProgressResponse {

    private WeeklyProgressResponse weeklyProgress;
    private List<GoalProgressResponse> goalProgress;
}
