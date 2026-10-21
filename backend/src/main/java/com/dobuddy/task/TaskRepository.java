package com.dobuddy.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByIdAndGoalUserId(Integer taskId, Integer userId);

    List<Task> findAllByGoalUserIdAndTaskDate(Integer userId, LocalDate taskDate);

    List<Task> findAllByGoalUserIdAndTaskDateBetween(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Task> findAllByGoalIdInAndTaskDateBetween(
            List<Integer> goalIds,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Task> findAllByGoalIdAndTaskDateBetween(
            Integer goalId,
            LocalDate startDate,
            LocalDate endDate
    );

    void deleteAllByGoalUserId(Integer userId);
    void deleteAllByGoalId(Integer goalId);
    long countByGoalId(Integer goalId);
    long countByGoalIdAndIsCompletedTrue(Integer goalId);
    boolean existsByGoalUserId(Integer userId);
    List<Task> findAllByGoalIdAndTaskDateBetweenOrderByIsCompletedAscCreatedAtAsc(
            Integer goalId,
            LocalDate weekStart,
            LocalDate weekEnd
    );
}
