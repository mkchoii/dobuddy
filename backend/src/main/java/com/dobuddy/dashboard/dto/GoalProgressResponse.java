package com.dobuddy.dashboard.dto;

import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalProgressResponse {

    private Integer goalId;
    private String goalName;
    private Category category;
    private Integer achievementRate;
}
