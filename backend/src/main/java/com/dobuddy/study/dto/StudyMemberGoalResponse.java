package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyMemberGoalResponse {

    private Integer goalId;
    private String goalName;
    private Category category;
    private List<StudyMemberTaskResponse> tasks;
}
