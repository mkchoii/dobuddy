package com.dobuddy.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GoalCreateResponse {

    private Integer goalId;
    private Category category;
    private String goalName;
    private LocalDateTime createdAt;

    @JsonProperty("isFirstGoal")
    private Boolean firstGoal;

    @JsonProperty("isSignupSession")
    private Boolean signupSession;

    private Long signupMinuteCount;
}
