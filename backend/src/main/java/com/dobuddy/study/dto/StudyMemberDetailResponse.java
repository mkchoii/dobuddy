package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyMemberDetailResponse {

    private Integer userId;
    private Integer studyMemberId;
    private String nickname;
    private Integer profileImageId;
    private int achievementRate;
    private List<StudyMemberGoalResponse> goals;
}
