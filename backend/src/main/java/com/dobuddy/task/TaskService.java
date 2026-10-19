package com.dobuddy.task;

import com.dobuddy.BusinessException;
import com.dobuddy.ProgressService;
import com.dobuddy.goal.Category;
import com.dobuddy.goal.Goal;
import com.dobuddy.goal.GoalRepository;
import com.dobuddy.goal.dto.TaskCreateResponse;
import com.dobuddy.study.StudyMemberGoal;
import com.dobuddy.study.StudyMemberGoalRepository;
import com.dobuddy.task.dto.*;
import com.dobuddy.user.User;
import com.dobuddy.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final ProgressService progressService;

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final StudyMemberGoalRepository studyMemberGoalRepository;

    @Transactional
    public TaskCreateResponse createTask(Integer userId, TaskCreateRequest request) {

        Goal goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "세부 목표를 찾을 수 없습니다."
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        Category category = goal.getCategory();

        StudyMemberGoal linkedGoal = studyMemberGoalRepository.findByGoalId(goal.getId());

        boolean isStudyLinked = linkedGoal != null;

        Integer linkedStudyId = isStudyLinked
                ? linkedGoal.getStudyMember().getStudy().getId()
                : null;

        boolean isFirstTask = user.getFirstTaskCreatedAt() == null;

        Task task = new Task(
                goal,
                request.getTaskDate(),
                request.getContent()
        );

        Task savedTask = taskRepository.save(task);

        if (isFirstTask) {
            user.setFirstTaskCreatedAt(savedTask.getCreatedAt());
        }

        long signupDayCount = ChronoUnit.DAYS.between(
                user.getCreatedAt().toLocalDate(),
                savedTask.getCreatedAt().toLocalDate()
        );

        long signupMinuteCount = Duration.between(
                user.getCreatedAt(),
                savedTask.getCreatedAt()
        ).toMinutes();

        ProgressResponse progress = progressService.getProgress(userId);

        return new TaskCreateResponse(
                savedTask.getId(),
                savedTask.getGoal().getId(),
                savedTask.getTaskDate(),
                savedTask.getContent(),
                savedTask.getCreatedAt(),
                savedTask.isCompleted(),
                progress.getWeeklyProgress(),
                progress.getGoalProgress(),
                category,
                isStudyLinked,
                linkedStudyId,
                isFirstTask,
                signupDayCount,
                signupMinuteCount
        );
    }

    @Transactional
    public TaskUpdateResponse updateTask(Integer userId, Integer taskId, TaskUpdateRequest request) {

        Task task = taskRepository.findByIdAndGoalUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "태스크를 찾을 수 없습니다."
                ));

        Goal goal = task.getGoal();

        task.updateContent(request.getContent());

        Task savedTask = taskRepository.save(task);

        StudyMemberGoal linkedGoal = studyMemberGoalRepository.findByGoalId(goal.getId());
        boolean isStudyLinked = linkedGoal != null;
        Integer linkedStudyId = isStudyLinked
                ? linkedGoal.getStudyMember().getStudy().getId()
                : null;

        long taskCreatedDayCount = ChronoUnit.DAYS.between(
                savedTask.getCreatedAt().toLocalDate(),
                LocalDate.now(ZoneId.of("Asia/Seoul"))
        );

        return new TaskUpdateResponse(
                savedTask.getId(),
                savedTask.getGoal().getId(),
                savedTask.getTaskDate(),
                savedTask.getContent(),
                savedTask.getCreatedAt(),
                savedTask.isCompleted(),
                goal.getCategory(),
                isStudyLinked,
                linkedStudyId,
                taskCreatedDayCount
        );
    }

    @Transactional
    public TaskDeleteResponse deleteTask(Integer userId, Integer taskId) {

        Task task = taskRepository.findByIdAndGoalUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "태스크를 찾을 수 없습니다."
                ));

        Goal goal = task.getGoal();

        StudyMemberGoal linkedGoal =
                studyMemberGoalRepository.findByGoalId(goal.getId());

        boolean isStudyLinked = linkedGoal != null;

        Integer linkedStudyId = isStudyLinked
                ? linkedGoal.getStudyMember().getStudy().getId()
                : null;

        long taskCreatedDayCount = ChronoUnit.DAYS.between(
                task.getCreatedAt().toLocalDate(),
                LocalDate.now(ZoneId.of("Asia/Seoul"))
        );

        boolean isTaskCompleted = task.isCompleted();

        taskRepository.delete(task);
        taskRepository.flush();

        ProgressResponse progress = progressService.getProgress(userId);

        TaskDeleteResponse response = new TaskDeleteResponse(
                progress.getWeeklyProgress(),
                progress.getGoalProgress(),
                taskId,
                goal.getId(),
                goal.getCategory(),
                isStudyLinked,
                linkedStudyId,
                isTaskCompleted,
                taskCreatedDayCount
        );

        return response;
    }

    @Transactional
    public TaskCompletionResponse completeTask(Integer userId, Integer taskId, TaskCompletionRequest request) {

        Task task = taskRepository.findByIdAndGoalUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "태스크를 찾을 수 없습니다."
                ));

        Goal goal = task.getGoal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        // 상태 변경 확인
        boolean isNewlyCompleted =
                !task.isCompleted() && request.getIsCompleted();

        boolean isNewlyCanceled =
                task.isCompleted() && !request.getIsCompleted();

        boolean isFirstCompletion =
                isNewlyCompleted && user.getFirstTaskCompletedAt() == null;

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 완료 취소 전 기존 완료 시각
        LocalDateTime previousCompletedAt = task.getCompletedAt();

        // 완료부터 취소까지 경과 시간
        Long completedMinuteCount = null;

        if (isNewlyCanceled && previousCompletedAt != null) {
            completedMinuteCount = Duration.between(
                    previousCompletedAt,
                    now
            ).toMinutes();
        }

        // 완료 상태 및 완료 시각 변경
        if (isNewlyCompleted || isNewlyCanceled) {
            task.updateCompleted(request.getIsCompleted(), now);
        }

        Task savedTask = taskRepository.save(task);

        // 최초 완료 시각 저장
        if (isFirstCompletion) {
            user.setFirstTaskCompletedAt(now);
        }

        // 스터디 연동 정보
        StudyMemberGoal linkedGoal =
                studyMemberGoalRepository.findByGoalId(goal.getId());

        boolean isStudyLinked = linkedGoal != null;

        Integer linkedStudyId = isStudyLinked
                ? linkedGoal.getStudyMember().getStudy().getId()
                : null;

        // 가입 후 경과 일수 (KST 날짜 기준)
        long signupDayCount = ChronoUnit.DAYS.between(
                user.getCreatedAt().toLocalDate(),
                now.toLocalDate()
        );

        // 가입 후 경과 분
        long signupMinuteCount = Duration.between(
                user.getCreatedAt(),
                now
        ).toMinutes();

        // 이벤트에 사용할 완료 시각
        LocalDateTime completedAt = null;

        if (isNewlyCompleted) {
            completedAt = now;
        } else if (isNewlyCanceled) {
            completedAt = previousCompletedAt;
        }

        ProgressResponse progress = progressService.getProgress(userId);

        return new TaskCompletionResponse(
                savedTask.getId(),
                savedTask.isCompleted(),
                progress.getWeeklyProgress(),
                progress.getGoalProgress(),
                goal.getId(),
                goal.getCategory(),
                isStudyLinked,
                linkedStudyId,
                isNewlyCompleted,
                isNewlyCanceled,
                isFirstCompletion,
                signupDayCount,
                signupMinuteCount,
                completedMinuteCount,
                completedAt
        );
    }
}
