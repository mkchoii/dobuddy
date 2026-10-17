package com.studymate.study;

import com.studymate.goal.Category;
import com.studymate.study.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studies")
public class StudyController {

    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<StudyResponse> createStudy(@Valid @RequestBody StudyCreateRequest request) {

        StudyResponse response = studyService.createStudy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{studyId}")
    public ResponseEntity<StudyResponse> updateStudy(@PathVariable Integer studyId, @Valid @RequestBody StudyUpdateRequest request) {

        StudyResponse response = studyService.updateStudy(studyId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studyId}/members/{studyMemberId}")
    public ResponseEntity<Void> deleteStudyMember(@PathVariable Integer studyId, @PathVariable Integer studyMemberId) {

        studyService.deleteStudyMember(studyId, studyMemberId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Integer studyId) {

        studyService.deleteStudy(studyId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<StudyListResponse> getStudies(@RequestParam(required = false) Category category, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        StudyListResponse response = studyService.getStudies(userId, category);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studyId}/matching-goals")
    public ResponseEntity<MatchingGoalListResponse> getMatchingGoals(@PathVariable Integer studyId, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        MatchingGoalListResponse response = studyService.getMatchingGoals(userId, studyId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{studyId}/members")
    public ResponseEntity<StudyJoinResponse> joinStudy(@PathVariable Integer studyId, @Valid @RequestBody StudyJoinRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        StudyJoinResponse response = studyService.joinStudy(userId, studyId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{studyId}/dashboard")
    public ResponseEntity<StudyDashboardResponse> getStudyDashboard(@PathVariable Integer studyId, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        StudyDashboardResponse response = studyService.getStudyDashboard(userId, studyId, isAdmin);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studyId}/members/{studyMemberId}")
    public ResponseEntity<StudyMemberDetailResponse> getStudyMemberDetail(@PathVariable Integer studyId, @PathVariable Integer studyMemberId, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        StudyMemberDetailResponse response = studyService.getStudyMemberDetail(studyId, studyMemberId, userId, isAdmin);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MyStudyResponse> getMyStudy(Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        MyStudyResponse response = studyService.getMyStudy(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{studyId}/goals")
    public ResponseEntity<Void> updateStudyGoal(@PathVariable Integer studyId, @Valid @RequestBody StudyGoalUpdateRequest request, Authentication authentication) {

        Integer userId = (Integer) authentication.getPrincipal();

        studyService.updateStudyGoal(studyId, userId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studyId}/availability")
    public ResponseEntity<StudyAvailabilityResponse> getStudyAvailability(@PathVariable Integer studyId) {

        StudyAvailabilityResponse response = studyService.getStudyAvailability(studyId);

        return ResponseEntity.ok(response);
    }
}
