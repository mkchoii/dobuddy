package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingGoalResponse {

    private Integer goalId;
    private String goalName;
    private Boolean isLinked;
}
