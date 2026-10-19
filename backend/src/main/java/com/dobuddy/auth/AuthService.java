package com.dobuddy.auth;

import com.dobuddy.BusinessException;
import com.dobuddy.EmailService;
import com.dobuddy.auth.dto.*;
import com.dobuddy.goal.GoalRepository;
import com.dobuddy.invite.InviteRepository;
import com.dobuddy.user.User;
import com.dobuddy.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoalRepository goalRepository;

    private final SecureRandom secureRandom = new SecureRandom();
    private final EmailService emailService;
    private final PasswordResetCountLimiter passwordResetCountLimiter;

    private static final List<String> ADJECTIVES = List.of(
            "배고픈",
            "졸린",
            "용감한",
            "신나는",
            "차분한",
            "똑똑한",
            "부지런한",
            "엉뚱한",
            "수줍은",
            "느긋한"
    );

    private static final List<String> ANIMALS = List.of(
            "호랑이",
            "토끼",
            "고양이",
            "강아지",
            "여우",
            "판다",
            "수달",
            "다람쥐",
            "펭귄",
            "사자",
            "독수리",
            "코끼리",
            "햄스터",
            "곰"
    );

    public EmailCheckResponse checkEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            return new EmailCheckResponse("LOGIN");
        }

        if (inviteRepository.existsByEmail(email)) {
            return new EmailCheckResponse("SIGNUP");
        }

        return new EmailCheckResponse("NOT_INVITED");
    }

    public SignupResponse signup(SignupRequest request) {

        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        if (!inviteRepository.existsByEmail(email)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "초대되지 않은 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String nickname = generateNickname();
        int profileImageId = generateProfileImageId();

        User user = new User(
                nickname,
                email,
                encodedPassword,
                profileImageId
        );

        user.updateAccessAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getCreatedAt()
        );
    }

    private String generateNickname() {

        while (true) {
            String adjective = ADJECTIVES.get((int) (Math.random() * ADJECTIVES.size()));
            String animal = ANIMALS.get((int) (Math.random() * ANIMALS.size()));
            String nickname = adjective + " " + animal;

            if (!userRepository.existsByNickname(nickname)) {
                return nickname;
            }
        }
    }

    private int generateProfileImageId() {
        return (int) (Math.random() * 6) + 1;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(
                    HttpStatus.UNAUTHORIZED,
                    "PASSWORD_MISMATCH",
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        boolean isFirstLogin = user.getAccessAt() == null;
        user.updateAccessAt(LocalDateTime.now());
        userRepository.save(user);

        boolean hasGoal = goalRepository.existsByUserId(user.getId());

        return new LoginResponse(
                user.getId(),
                user.getRole(),
                hasGoal,
                isFirstLogin
        );
    }

    @Transactional
    public void resetPassword(EmailCheckRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.UNAUTHORIZED,
                        "DB_FAILED",
                        "사용자 정보 조회에 실패했습니다.")
                );

        // 하루 5회 요청 제한
        if (!passwordResetCountLimiter.tryAcquire(user.getEmail())) {
            throw new BusinessException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "DAILY_LIMIT",
                    "비밀번호 재설정은 하루 최대 5회까지 가능합니다."
            );
        }

        // 임시 비밀번호 생성
        String tempPassword = String.format("%04d", secureRandom.nextInt(10000));

        // 이메일 발송
        try {
            emailService.sendTemporaryPassword(request.getEmail(), tempPassword);
        } catch (MailException e) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "MAIL_FAILED",
                    "임시 비밀번호 메일 발송에 실패했습니다."
            );
        }

        // 기존 비밀번호 변경
        try {
            user.updatePassword(passwordEncoder.encode(tempPassword));
            userRepository.saveAndFlush(user);
        } catch (DataAccessException e) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DB_FAILED",
                    "비밀번호 재설정에 실패했습니다."
            );
        }
    }
}
