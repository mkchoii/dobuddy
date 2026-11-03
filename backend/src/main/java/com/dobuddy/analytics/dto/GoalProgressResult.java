package com.dobuddy.analytics.dto;

public interface GoalProgressResult extends ProgressCountResult {

    Integer getGoalId();
    String getCategory();
}
