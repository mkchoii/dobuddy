package com.dobuddy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private String nickname;
    private String email;
    private Integer profileImageId;
}
