package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyDashboardResponse {

    private Integer studyId;
    private String studyName;
    private Category category;
    private long currentMembers;
    private Integer maxMembers;
    private List<StudyMemberResponse> members;
}
