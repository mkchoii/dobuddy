package com.dobuddy.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.dashboard.dto.GoalProgressResponse;
import com.dobuddy.dashboard.dto.WeeklyProgressResponse;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TaskCompletionResponse {

    private Integer taskId;

    @JsonProperty("isCompleted")
    private boolean completed;

    private WeeklyProgressResponse weeklyProgress;
    private List<GoalProgressResponse> goalProgress;

    private Integer goalId;
    private Category category;

    @JsonProperty("isStudyLinked")
    private boolean studyLinked;

    private Integer linkedStudyId;

    @JsonProperty("isNewlyCompleted")
    private boolean newlyCompleted;

    @JsonProperty("isNewlyCanceled")
    private boolean newlyCanceled;

    @JsonProperty("isFirstCompletion")
    private boolean firstCompletion;

    private long signupDayCount;
    private long signupMinuteCount;
    private Long completedMinuteCount;
    private LocalDateTime completedAt;
}
