package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyMemberDeleteResponse {

    private Integer userId;
    private Integer studyId;
    private long joinDayCount;
    private long memberCount;
}
