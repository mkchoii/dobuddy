package com.dobuddy.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntryStatusResponse {

    @JsonProperty("isLoggedIn")
    boolean loggedIn;

    @JsonProperty("hasAuthHistory")
    boolean authHistory;
}
