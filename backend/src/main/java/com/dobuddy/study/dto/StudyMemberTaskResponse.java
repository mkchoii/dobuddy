package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StudyMemberTaskResponse {

    private Integer taskId;
    private String content;
    private boolean isCompleted;
}
