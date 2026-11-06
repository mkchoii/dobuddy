package com.dobuddy.invite.dto;

import com.dobuddy.user.Channel;
import lombok.Getter;

@Getter
public class InviteCreateRequest {

    private String email;
    private Channel channel;
}
