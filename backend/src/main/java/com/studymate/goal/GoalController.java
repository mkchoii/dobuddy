package com.studymate.goal;

import com.studymate.goal.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalCreateRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        GoalResponse response = goalService.createGoal(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<GoalListResponse> getGoals(Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        GoalListResponse response = goalService.getGoals(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Integer goalId, @Valid @RequestBody GoalUpdateRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        GoalResponse response = goalService.updateGoal(userId, goalId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Integer goalId, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        goalService.deleteGoal(userId, goalId);

        return ResponseEntity.noContent().build();
    }

}
