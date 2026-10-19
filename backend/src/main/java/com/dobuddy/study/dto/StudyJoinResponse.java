package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyJoinResponse {

    private Integer studyId;
    private Integer studyMemberId;
    private boolean joined;
    private List<Integer> goalIds;
}
