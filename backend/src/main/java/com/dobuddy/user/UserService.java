package com.dobuddy.user;

import com.dobuddy.BusinessException;
import com.dobuddy.goal.GoalRepository;
import com.dobuddy.invite.InviteRepository;
import com.dobuddy.study.StudyMemberGoalRepository;
import com.dobuddy.study.StudyMemberRepository;
import com.dobuddy.task.TaskRepository;
import com.dobuddy.user.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final InviteRepository inviteRepository;
    private final StudyMemberGoalRepository studyMemberGoalRepository;

    public FinalGoalResponse updateFinalGoal(Integer userId, FinalGoalRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        user.updateFinalGoal(request.getFinalGoal());

        User savedUser = userRepository.save(user);

        return new FinalGoalResponse(
                savedUser.getFinalGoal()
        );
    }

    public UserInfoResponse getUserInfo(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        return new UserInfoResponse(
                user.getNickname(),
                user.getEmail(),
                user.getProfileImageId()
        );
    }

    public ProfileImageResponse updateProfileImage(Integer userId, ProfileImageRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        user.updateProfileImage(request.getProfileImageId());

        User savedUser = userRepository.save(user);

        return new ProfileImageResponse(
                savedUser.getProfileImageId()
        );
    }

    public void updatePassword(Integer userId, PasswordUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "현재 비밀번호가 일치하지 않습니다."
            );
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "새 비밀번호는 현재 비밀번호와 다르게 설정해주세요."
            );
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        user.updatePassword(encodedPassword);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        taskRepository.deleteAllByGoalUserId(userId);
        studyMemberGoalRepository.deleteAllByUserId(userId);
        goalRepository.deleteAllByUserId(userId);
        studyMemberRepository.deleteAllByUserId(userId);

        userRepository.delete(user);
    }
}
