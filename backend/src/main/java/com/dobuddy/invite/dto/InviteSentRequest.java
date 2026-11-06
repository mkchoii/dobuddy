package com.dobuddy.invite.dto;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class InviteSentRequest {

    private String email;
    private OffsetDateTime sentAt;
}
