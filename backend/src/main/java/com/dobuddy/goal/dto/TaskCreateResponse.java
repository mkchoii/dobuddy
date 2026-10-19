package com.dobuddy.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.dashboard.dto.GoalProgressResponse;
import com.dobuddy.dashboard.dto.WeeklyProgressResponse;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TaskCreateResponse {

    private Integer taskId;
    private Integer goalId;
    private LocalDate taskDate;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isCompleted;
    private WeeklyProgressResponse weeklyProgress;
    private List<GoalProgressResponse> goalProgress;

    private Category category;

    @JsonProperty("isStudyLinked")
    private boolean studyLinked;

    private Integer linkedStudyId;

    @JsonProperty("isFirstTask")
    private boolean firstTask;

    private long signupDayCount;
    private long signupMinuteCount;
}
