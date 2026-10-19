package com.dobuddy.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GoalListResponse {

    private List<GoalResponse> goals;
}
