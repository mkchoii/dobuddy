package com.dobuddy.auth;

import com.dobuddy.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("/auth/email-check")
    public ResponseEntity<EmailCheckResponse> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        EmailCheckResponse response = authService.checkEmail(request.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        SignupResponse response = authService.signup(request);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        response.getUserId(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        // 로그인 이력 쿠키 발급
        ResponseCookie cookie = ResponseCookie.from("hasAuthHistory", "true")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(90))
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        HttpSession session = httpRequest.getSession();
        session.setAttribute("isSignupSession", true);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        LoginResponse response = authService.login(request);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        response.getUserId(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_" + response.getRole().name())
                        )
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        // 로그인 이력 쿠키 발급
        ResponseCookie cookie = ResponseCookie.from("hasAuthHistory", "true")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(90))
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/login/new-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(@Valid @RequestBody EmailCheckRequest request) {

        PasswordResetResponse response = authService.resetPassword(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/entry-status")
    public ResponseEntity<EntryStatusResponse> getEntryStatus(Authentication authentication,
                                                              @CookieValue(value = "hasAuthHistory", required = false) String hasAuthHistory) {

        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Integer;

        boolean hasHistory = "true".equals(hasAuthHistory);

        return ResponseEntity.ok(
                new EntryStatusResponse(
                        isLoggedIn,
                        hasHistory
                )
        );

    }
}
