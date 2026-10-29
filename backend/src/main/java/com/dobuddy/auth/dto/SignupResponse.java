package com.dobuddy.auth.dto;

import com.dobuddy.user.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private Integer userId;
    private String email;
    private String nickname;
    private LocalDateTime signupAt;
    private Channel channel;
}
