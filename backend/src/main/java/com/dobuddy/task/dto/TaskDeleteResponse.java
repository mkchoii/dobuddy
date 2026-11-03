package com.dobuddy.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.dashboard.dto.GoalProgressResponse;
import com.dobuddy.dashboard.dto.WeeklyProgressResponse;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskDeleteResponse {

    private WeeklyProgressResponse weeklyProgress;
    private List<GoalProgressResponse> goalProgress;

    private Integer taskId;
    private Integer goalId;
    private Category category;

    @JsonProperty("isStudyLinked")
    private boolean studyLinked;
    private Integer linkedStudyId;

    @JsonProperty("isTaskCompleted")
    private boolean taskCompleted;
    private long taskCreatedDayCount;
}
