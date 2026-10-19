package com.dobuddy.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TaskCompletionRequest {

    @NotNull(message = "완료 상태를 입력해주세요.")
    private Boolean isCompleted;
}
