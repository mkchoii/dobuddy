package com.dobuddy.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TaskUpdateRequest {

    @NotBlank(message = "태스크를 입력해주세요.")
    private String content;
}
