package com.dobuddy;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final Integer attemptCount;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.code = null;
        this.attemptCount = null;
    }

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.attemptCount = null;
    }

    public BusinessException(HttpStatus status, String code, String message, Integer attemptCount) {
        super(message);
        this.status = status;
        this.code = code;
        this.attemptCount = attemptCount;
    }
}
