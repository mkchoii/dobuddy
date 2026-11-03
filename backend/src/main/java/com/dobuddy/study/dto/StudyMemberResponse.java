package com.dobuddy.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyMemberResponse {

    private Integer userId;
    private Integer studyMemberId;
    private String nickname;
    private Integer profileImageId;
    private boolean isMe;
    private int achievementRate;
}
