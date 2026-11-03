package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyListResponse {

    private List<StudyListItemResponse> studies;
}
