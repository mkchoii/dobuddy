package com.studymate.auth;

import com.studymate.BusinessException;
import com.studymate.EmailService;
import com.studymate.auth.dto.*;
import com.studymate.goal.GoalRepository;
import com.studymate.invite.InviteRepository;
import com.studymate.user.User;
import com.studymate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname()
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
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        boolean hasGoal = goalRepository.existsByUserId(user.getId());

        return new LoginResponse(user.getId(), user.getRole(), hasGoal);
    }

    public void resetPassword(EmailCheckRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.UNAUTHORIZED,
                        "이메일이 올바르지 않습니다.")
                );

        if (user == null) {
            return;
        }

        // 임시 비밀번호 생성
        String tempPassword = String.format("%04d", secureRandom.nextInt(10000));
        emailService.sendTemporaryPassword(request.getEmail(), tempPassword);

        user.updatePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
    }
}
