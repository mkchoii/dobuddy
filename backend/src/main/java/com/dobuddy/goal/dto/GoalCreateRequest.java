package com.dobuddy.goal.dto;

import com.dobuddy.goal.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GoalCreateRequest {

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @NotBlank(message = "세부 목표명을 입력해주세요.")
    private String goalName;
}
