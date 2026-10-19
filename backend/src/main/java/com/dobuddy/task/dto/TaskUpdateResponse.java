package com.dobuddy.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskUpdateResponse {

    private Integer taskId;
    private Integer goalId;
    private LocalDate taskDate;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isCompleted;

    private Category category;

    @JsonProperty("isStudyLinked")
    private boolean studyLinked;

    private Integer linkedStudyId;
    private long taskCreatedDayCount;
}
