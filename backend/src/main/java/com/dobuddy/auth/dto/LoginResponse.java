package com.dobuddy.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dobuddy.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Integer userId;
    private Role role;
    private boolean hasGoal;

    @JsonProperty("isFirstLogin")
    private boolean firstLogin;
}
