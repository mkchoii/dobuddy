package com.dobuddy.goal;

import com.dobuddy.goal.dto.*;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<GoalCreateResponse> createGoal(@Valid @RequestBody GoalCreateRequest request, Authentication authentication, HttpSession session) {

        Integer userId = (Integer) authentication.getPrincipal();

        GoalCreateResponse response = goalService.createGoal(userId, request, session);

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
    public ResponseEntity<GoalUpdateResponse> updateGoal(@PathVariable Integer goalId, @Valid @RequestBody GoalUpdateRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        GoalUpdateResponse response = goalService.updateGoal(userId, goalId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<GoalDeleteResponse> deleteGoal(@PathVariable Integer goalId, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        GoalDeleteResponse response = goalService.deleteGoal(userId, goalId);

        return ResponseEntity.ok(response);
    }

}
