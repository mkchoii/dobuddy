package com.studymate.goal;

import com.studymate.BusinessException;
import com.studymate.goal.dto.*;
import com.studymate.study.StudyMember;
import com.studymate.study.StudyMemberGoalRepository;
import com.studymate.study.StudyMemberRepository;
import com.studymate.task.TaskRepository;
import com.studymate.user.User;
import com.studymate.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final StudyMemberGoalRepository studyMemberGoalRepository;

    @Transactional
    public GoalCreateResponse createGoal(Integer userId, GoalCreateRequest request, HttpSession session) {

        long goalCount = goalRepository.countByUserId(userId);

        if (goalCount >= 3) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "세부목표는 최대 3개까지만 설정할 수 있습니다."
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        boolean isFirstGoal = !goalRepository.existsByUserId(userId);

        boolean isSignupSession = Boolean.TRUE.equals(session.getAttribute("isSignupSession"));

        Goal goal = new Goal(
                request.getCategory(),
                request.getGoalName(),
                user
        );

        Goal savedGoal = goalRepository.save(goal);

        long signupMinuteCount = Math.max(0, Duration.between(
                user.getCreatedAt(),
                savedGoal.getCreatedAt()
            ).toMinutes()
        );

        return new GoalCreateResponse(
                savedGoal.getId(),
                savedGoal.getCategory(),
                savedGoal.getGoalName(),
                savedGoal.getCreatedAt(),
                isFirstGoal,
                isSignupSession,
                signupMinuteCount
        );
    }

    public GoalListResponse getGoals(Integer userId) {

        List<GoalResponse> goals = goalRepository.findAllByUserId(userId)
                .stream()
                .map(goal -> new GoalResponse(
                        goal.getId(),
                        goal.getCategory(),
                        goal.getGoalName(),
                        goal.getCreatedAt()
                ))
                .toList();

        return new GoalListResponse(goals);
    }

    @Transactional
    public GoalResponse updateGoal(Integer userId, Integer goalId, GoalUpdateRequest request) {

        if (request.getCategory() == null && request.getGoalName() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "수정할 내용을 입력해주세요."
            );
        }

        if (request.getGoalName() != null && request.getGoalName().isBlank()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "세부 목표명을 입력해주세요."
            );
        }

        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "목표를 찾을 수 없습니다."
                ));

        if (request.getCategory() != null && !request.getCategory().equals(goal.getCategory())) {
            if (studyMemberGoalRepository.existsByGoalId(goalId)) {
                throw new BusinessException(
                        HttpStatus.CONFLICT,
                        "연동된 세부목표의 카테고리는 변경할 수 없습니다."
                );
            }
            goal.updateCategory(request.getCategory());
        }

        if (request.getGoalName() != null) {
            goal.updateGoalName(request.getGoalName());
        }

        Goal savedGoal = goalRepository.save(goal);

        return new GoalResponse(
                savedGoal.getId(),
                savedGoal.getCategory(),
                savedGoal.getGoalName(),
                savedGoal.getCreatedAt()
        );
    }

    @Transactional
    public void deleteGoal(Integer userId, Integer goalId) {

        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "세부 목표를 찾을 수 없습니다."
                ));

        long goalCount = goalRepository.countByUserId(userId);

        if (goalCount <= 1) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "세부목표는 최소 1개 이상 존재해야 합니다."
            );
        }

        if (studyMemberGoalRepository.existsByGoalId(goalId)) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "연동된 세부목표는 삭제할 수 없습니다."
            );
        }

        taskRepository.deleteAllByGoalId(goalId);
        goalRepository.delete(goal);
    }
}
