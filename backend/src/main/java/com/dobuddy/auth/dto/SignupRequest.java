package com.dobuddy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank(message = "이메일이 올바르지 않습니다.")
    @Email(message = "이메일이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 숫자 4자리입니다.")
    @Pattern(
            regexp = "^[0-9]{4}$",
            message = "비밀번호는 숫자 4자리입니다."
    )
    private String password;
}
