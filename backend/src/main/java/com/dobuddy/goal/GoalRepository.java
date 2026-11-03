package com.dobuddy.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Integer> {

    List<Goal> findAllByUserId(Integer userId);
    Optional<Goal> findByIdAndUserId(Integer goalId, Integer userId);
    boolean existsByUserIdAndCategory(Integer userId, Category category);
    List<Goal> findAllByUserIdAndCategory(Integer userId, Category category);
    boolean existsByUserId(Integer userId);
    void deleteAllByUserId(Integer userId);
    long countByUserId(Integer userId);
    List<Goal> findAllByUserIdOrderByCreatedAtAsc(Integer userId);

}
