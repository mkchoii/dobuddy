package com.dobuddy.user;

import com.dobuddy.user.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/final-goal")
    public ResponseEntity<FinalGoalResponse> updateFinalGoal(@Valid @RequestBody FinalGoalRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        FinalGoalResponse response = userService.updateFinalGoal(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UserInfoResponse> getUserInfo(Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        UserInfoResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<ProfileImageResponse> updateUserProfileImage(@Valid @RequestBody ProfileImageRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        ProfileImageResponse response = userService.updateProfileImage(userId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        userService.updatePassword(userId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {

        Integer userId = (Integer) authentication.getPrincipal();

        userService.deleteUser(userId);

        // 인증 정보 제거
        SecurityContextHolder.clearContext();

        // 서버 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 브라우저의 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }
}
