package com.dobuddy;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;
    private final Integer attemptCount;

    public ErrorResponse(String message) {
        this.message = message;
        this.code = null;
        this.attemptCount = null;
    }

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
        this.attemptCount = null;
    }

    public ErrorResponse(String message, String code, Integer attemptCount) {
        this.message = message;
        this.code = code;
        this.attemptCount = attemptCount;
    }
}