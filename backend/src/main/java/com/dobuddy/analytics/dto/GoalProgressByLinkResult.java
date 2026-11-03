package com.dobuddy.analytics.dto;

public interface GoalProgressByLinkResult extends ProgressCountResult {

    Integer getGoalId();
    Boolean getLinked();
}
