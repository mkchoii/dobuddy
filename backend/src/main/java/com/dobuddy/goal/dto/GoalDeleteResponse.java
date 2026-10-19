package com.dobuddy.goal.dto;

import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalDeleteResponse {

    private Integer goalId;
    private Category category;
    private long goalCreatedDayCount;
    private long taskCount;
    private long completedTaskCount;
}
