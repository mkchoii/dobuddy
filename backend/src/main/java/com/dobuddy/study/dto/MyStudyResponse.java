package com.dobuddy.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyStudyResponse {

    private Integer studyId;
    private boolean chatPromoSeen;
}
