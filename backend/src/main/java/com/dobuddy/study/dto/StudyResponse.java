package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StudyResponse {

    private Integer studyId;
    private String studyName;
    private Category category;
    private long currentMembers;
    private Integer maxMembers;
    private LocalDateTime createdAt;
}
