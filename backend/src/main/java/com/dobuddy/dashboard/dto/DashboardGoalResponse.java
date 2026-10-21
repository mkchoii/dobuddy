package com.dobuddy.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardGoalResponse {

    private Integer goalId;
    private String goalName;
    private Category category;

    @JsonProperty("isLinked")
    private boolean linked;

    private List<DashboardTaskResponse> tasks;
}
