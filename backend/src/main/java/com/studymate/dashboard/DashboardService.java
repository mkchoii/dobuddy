package com.studymate.dashboard;

import com.studymate.BusinessException;
import com.studymate.ProgressService;
import com.studymate.dashboard.dto.*;
import com.studymate.goal.Goal;
import com.studymate.goal.GoalRepository;
import com.studymate.study.StudyMemberGoal;
import com.studymate.study.StudyMemberGoalRepository;
import com.studymate.task.Task;
import com.studymate.task.TaskRepository;
import com.studymate.task.dto.ProgressResponse;
import com.studymate.user.User;
import com.studymate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProgressService progressService;

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final StudyMemberGoalRepository studyMemberGoalRepository;

    public DashboardResponse getDashboard(Integer userId, LocalDate date) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        List<Goal> goals = goalRepository.findAllByUserId(userId);

        ProgressResponse progress = progressService.getProgress(userId);

        // 선택한 날짜의 태스크 조회
        List<Task> dailyTasks = taskRepository.findAllByGoalUserIdAndTaskDate(
                userId,
                date
        );

        List<DashboardGoalResponse> dashboardGoals = goals.stream()
                .map(goal -> {
                        boolean isLinked = studyMemberGoalRepository.existsByGoalId(goal.getId());
                        List<DashboardTaskResponse> tasks = dailyTasks.stream()
                                .filter(task ->
                                        task.getGoal().getId().equals(goal.getId())
                                )
                                .map(task -> new DashboardTaskResponse(
                                        task.getId(),
                                        task.getContent(),
                                        task.isCompleted(),
                                        task.getCreatedAt()
                                ))
                                .toList();

                        return new DashboardGoalResponse(
                                goal.getId(),
                                goal.getGoalName(),
                                goal.getCategory(),
                                isLinked,
                                tasks
                        );
                })
                .toList();

        return new DashboardResponse(
                user.getFinalGoal(),
                user.getNickname(),
                date,
                progress.getWeeklyProgress(),
                progress.getGoalProgress(),
                dashboardGoals
        );
    }
}
