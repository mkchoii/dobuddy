package com.dobuddy.study.dto;

import com.dobuddy.goal.Category;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class StudyCreateRequest {

    @NotBlank(message = "스터디명을 입력해주세요.")
    @Size(max = 10, message = "스터디명은 10자 이하여야 합니다.")
    private String studyName;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @NotNull(message = "정원을 입력해주세요.")
    @Min(value = 2, message = "정원은 2명 이상 20명 이하입니다.")
    @Max(value = 20, message = "정원은 2명 이상 20명 이하입니다.")
    private Integer maxMembers;
}
