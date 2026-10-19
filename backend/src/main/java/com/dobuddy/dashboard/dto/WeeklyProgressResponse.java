package com.dobuddy.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeeklyProgressResponse {

    private int achievementRate;
    private int inProgressCount;
    private int completedCount;
}
