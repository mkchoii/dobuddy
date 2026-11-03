package com.dobuddy.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalUpdateResponse {

    private Integer goalId;
    private Category category;
    private String goalName;

    @JsonProperty("isGoalCategoryUpdated")
    private boolean goalCategoryUpdated;

    @JsonProperty("isGoalNameUpdated")
    private boolean goalNameUpdated;

    private long goalCreatedDayCount;

    @JsonProperty("isStudyLinked")
    private boolean studyLinked;

    private Integer linkedStudyId;
}
