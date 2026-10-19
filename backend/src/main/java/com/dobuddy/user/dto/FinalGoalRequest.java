package com.dobuddy.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FinalGoalRequest {

    @NotBlank(message = "최종 목표를 입력해주세요.")
    private String finalGoal;
}
