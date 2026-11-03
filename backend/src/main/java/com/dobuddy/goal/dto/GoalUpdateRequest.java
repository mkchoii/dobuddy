package com.dobuddy.goal.dto;

import com.dobuddy.goal.Category;
import lombok.Getter;

@Getter
public class GoalUpdateRequest {

    private Category category;
    private String goalName;
}
