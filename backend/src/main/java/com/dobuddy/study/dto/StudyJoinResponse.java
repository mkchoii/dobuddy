package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class StudyJoinResponse {

    private Integer studyId;
    private Integer studyMemberId;
    private boolean joined;
    private List<Integer> goalIds;
    private Category category;
    private long memberCount;

    @JsonProperty("isFirstStudy")
    private boolean firstStudy;

    private LocalDateTime firstStudyJoinedAt;
    private long firstGoalMinuteCount;
    private long signupDayCount;
}
