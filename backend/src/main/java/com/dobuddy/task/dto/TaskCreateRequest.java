package com.dobuddy.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskCreateRequest {

    private Integer goalId;
    private LocalDate taskDate;

    @NotBlank(message = "태스크 내용을 입력하세요.")
    private String content;
}
