package com.dobuddy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailCheckRequest {

    @NotBlank(message = "이메일이 올바르지 않습니다.")
    @Email(message = "이메일이 올바르지 않습니다.")
    private String email;
}
