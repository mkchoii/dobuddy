package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyListItemResponse {

    private Integer studyId;
    private String studyName;
    private Category category;
    private long currentMembers;
    private Integer maxMembers;
    private boolean canJoin;
}
