package com.dobuddy;

import com.dobuddy.dashboard.dto.GoalProgressResponse;
import com.dobuddy.dashboard.dto.WeeklyProgressResponse;
import com.dobuddy.goal.Goal;
import com.dobuddy.goal.GoalRepository;
import com.dobuddy.task.Task;
import com.dobuddy.task.TaskRepository;
import com.dobuddy.task.dto.ProgressResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public ProgressResponse getProgress(Integer userId) {

        // 주간 범위 계산(KST)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate weekStart =
                today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd =
                today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 주간 태스크 조회
        List<Task> weeklyTasks = taskRepository.findAllByGoalUserIdAndTaskDateBetween(
                userId,
                weekStart,
                weekEnd
        );

        List<Goal> goals = goalRepository.findAllByUserId(userId);

        // 달성률 계산

        int totalCount = weeklyTasks.size();
        int completedCount = (int) weeklyTasks.stream()
                .filter(Task::isCompleted)
                .count();
        int inProgressCount = totalCount - completedCount;
        int achievementRate = totalCount == 0 ? 0 : (int) (completedCount * 100.0 / totalCount);

         WeeklyProgressResponse weeklyProgress = new WeeklyProgressResponse(
                achievementRate,
                inProgressCount,
                completedCount
        );

        List<GoalProgressResponse> goalProgress = goals.stream()
                .map(goal -> {
                    List<Task> goalWeeklyTasks = weeklyTasks.stream()
                            .filter(task ->
                                    task.getGoal().getId().equals(goal.getId())
                            )
                            .toList();

                    int goalTotalCount = goalWeeklyTasks.size();
                    int goalCompletedCount = (int) goalWeeklyTasks.stream()
                            .filter(Task::isCompleted)
                            .count();
                    Integer goalAchievementRate = goalTotalCount == 0 ? null : (int) (goalCompletedCount * 100.0 / goalTotalCount);

                    return new GoalProgressResponse(
                            goal.getId(),
                            goal.getGoalName(),
                            goal.getCategory(),
                            goalAchievementRate
                    );
                })
                .toList();

        return new ProgressResponse(
                weeklyProgress,
                goalProgress
        );
    }
}
