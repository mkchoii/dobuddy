package com.studymate.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.studymate.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Integer userId;
    private Role role;
    private boolean hasGoal;

    @JsonProperty("isFirstLogin")
    private boolean firstLogin;
}
