package com.dobuddy.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProfileImageRequest {

    @NotNull(message = "프로필 이미지를 선택해주세요.")
    private Integer profileImageId;
}
