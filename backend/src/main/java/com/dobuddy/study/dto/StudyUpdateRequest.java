package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class StudyUpdateRequest {

    @Size(max = 10, message = "스터디명은 10자 이하여야 합니다.")
    private String studyName;

    private Category category;

    @Min(value = 2, message = "정원은 2명 이상 20명 이하입니다.")
    @Max(value = 20, message = "정원은 2명 이상 20명 이하입니다.")
    private Integer maxMembers;
}
