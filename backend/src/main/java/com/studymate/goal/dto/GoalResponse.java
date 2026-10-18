package com.studymate.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.studymate.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GoalResponse {

    private Integer goalId;
    private Category category;
    private String goalName;
    private LocalDateTime createdAt;
}
