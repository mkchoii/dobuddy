package com.dobuddy.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordUpdateRequest {

    @NotBlank(message = "비밀번호는 숫자 4자리입니다.")
    @Pattern(regexp = "^[0-9]{4}$",
            message = "비밀번호는 숫자 4자리입니다.")
    private String currentPassword;

    @NotBlank(message = "비밀번호는 숫자 4자리입니다.")
    @Pattern(regexp = "^[0-9]{4}$",
            message = "비밀번호는 숫자 4자리입니다.")
    private String newPassword;
}
