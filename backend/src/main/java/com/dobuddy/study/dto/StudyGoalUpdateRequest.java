package com.dobuddy.study.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class StudyGoalUpdateRequest {

    @NotEmpty(message = "세부목표를 최소 1개 이상 연동해야 합니다.")
    private List<Integer> goalIds;
}
