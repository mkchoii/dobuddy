package com.studymate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;

    public ErrorResponse(String message) {
        this.message = message;
        this.code = null;
    }

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}