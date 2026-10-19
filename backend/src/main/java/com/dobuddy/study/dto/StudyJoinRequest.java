package com.dobuddy.study.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class StudyJoinRequest {

    @NotEmpty(message = "연동할 세부목표를 선택하세요.")
    private List<Integer> goalIds;
}
