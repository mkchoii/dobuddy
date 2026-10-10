package com.studymate.study;

import com.studymate.BusinessException;
import com.studymate.goal.Category;
import com.studymate.goal.Goal;
import com.studymate.goal.GoalRepository;
import com.studymate.study.dto.*;
import com.studymate.task.Task;
import com.studymate.task.TaskRepository;
import com.studymate.user.User;
import com.studymate.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final StudyMemberGoalRepository studyMemberGoalRepository;
    private final TaskRepository taskRepository;

    public StudyResponse createStudy(StudyCreateRequest request) {

        Study study = new Study(
                request.getStudyName(),
                request.getCategory(),
                request.getMaxMembers()
        );

        Study savedStudy = studyRepository.save(study);

        return new StudyResponse(
                savedStudy.getId(),
                savedStudy.getStudyName(),
                savedStudy.getCategory(),
                0,
                savedStudy.getMaxMembers(),
                savedStudy.getCreatedAt()
        );
    }

    public StudyResponse updateStudy(Integer studyId, StudyUpdateRequest request) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디를 찾을 수 없습니다."
                ));

        if (request.getStudyName() != null) {
            study.updateStudyName(request.getStudyName());
        }

        if (request.getCategory() != null) {
            study.updateCategory(request.getCategory());
        }

        if (request.getMaxMembers() != null) {
            study.updateMaxMembers(request.getMaxMembers());
        }

        Study savedStudy = studyRepository.save(study);

        long currentMembers =  studyMemberRepository.countByStudyId(studyId);

        return new StudyResponse(
                savedStudy.getId(),
                savedStudy.getStudyName(),
                savedStudy.getCategory(),
                currentMembers,
                savedStudy.getMaxMembers(),
                savedStudy.getCreatedAt()
        );
    }

    @Transactional
    public void deleteStudyMember(Integer studyId, Integer studyMemberId) {

        StudyMember studyMember = studyMemberRepository.findByIdAndStudyId(studyMemberId, studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디 회원을 찾을 수 없습니다."
                ));

        studyMemberGoalRepository.deleteAllByStudyMemberId(studyMember.getId());
        studyMemberRepository.delete(studyMember);
    }

    @Transactional
    public void deleteStudy(Integer studyId) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디를 찾을 수 없습니다."
                ));

        List<StudyMember> studyMembers = studyMemberRepository.findAllByStudyId(studyId);

        for (StudyMember studyMember : studyMembers) {
            studyMemberGoalRepository.deleteAllByStudyMemberId(studyMember.getId());
        }

        studyMemberRepository.deleteAll(studyMembers);
        studyRepository.delete(study);
    }

    public StudyListResponse getStudies(Integer userId, Category category) {

        List<Study> studies;

        if (category == null) {
            studies = studyRepository.findAll();
        } else {
            studies = studyRepository.findAllByCategory(category);
        }

        List<StudyListItemResponse> studyResponses = studies.stream()
                .map(study -> {
                    long currentMembers = studyMemberRepository.countByStudyId(study.getId());
                    boolean hasMatchingGoal = goalRepository.existsByUserIdAndCategory(userId, study.getCategory());
                    boolean alreadyJoined = studyMemberRepository.existsByStudyIdAndUserId(study.getId(), userId);

                    // 가입 가능 조건 : 현재 가입인원 정원 미만, 같은 카테고리의 세부목표 보유, 이미 가입하지 않음
                    boolean canJoin = currentMembers < study.getMaxMembers() && hasMatchingGoal && !alreadyJoined;

                    return new StudyListItemResponse(
                            study.getId(),
                            study.getStudyName(),
                            study.getCategory(),
                            currentMembers,
                            study.getMaxMembers(),
                            canJoin
                    );
                })
                .toList();

        return new StudyListResponse(studyResponses);
    }

    public MatchingGoalListResponse getMatchingGoals(Integer userId, Integer studyId) {

       // 해당 유저가 가진 세부목표 카테고리와 스터디의 카테고리 일치하는지 확인. hasMatchingGoal=true면 유저가 가진 해당 카테고리의 세부목표 가져옴.
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디를 찾을 수 없습니다."
                ));

        List<Goal> matchingGoals = goalRepository.findAllByUserIdAndCategory(userId, study.getCategory());

        List<MatchingGoalResponse> goalResponses = matchingGoals.stream()
                .map(goal -> {
                    return new MatchingGoalResponse(
                            goal.getId(),
                            goal.getGoalName()
                    );
                })
                .toList();

        return new MatchingGoalListResponse(
                studyId,
                goalResponses
        );
    }

    public StudyJoinResponse joinStudy(Integer userId, Integer studyId, StudyJoinRequest request) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디를 찾을 수 없습니다."
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if (studyMemberRepository.existsByUserId(userId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "이미 가입한 스터디가 있습니다."
            );
        }

        if (studyMemberRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "이미 참여중인 스터디입니다."
            );
        }

        long currentMembers = studyMemberRepository.countByStudyId(studyId);

        if (currentMembers >= study.getMaxMembers()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "스터디 정원이 초과되어 참여할 수 없습니다."
            );
        }

        List<Goal> goals = goalRepository.findAllById(request.getGoalIds());

        if (goals.size() != request.getGoalIds().size()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "존재하지 않는 세부목표가 포함되어 있습니다."
            );
        }

        boolean invalidGoal = goals.stream()
                .anyMatch(goal ->
                        !goal.getUser().getId().equals(userId) || goal.getCategory() != study.getCategory()
                );

        if (invalidGoal) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "연동할 수 없는 세부목표가 포함되어 있습니다."
            );
        }

        StudyMember studyMember = studyMemberRepository.save(new StudyMember(study, user));

        List<StudyMemberGoal> memberGoals = goals.stream()
                        .map(goal -> new StudyMemberGoal(studyMember, goal))
                                .toList();

        studyMemberGoalRepository.saveAll(memberGoals);

        return new StudyJoinResponse(
                studyId,
                studyMember.getId(),
                true,
                request.getGoalIds()
        );
    }

    public StudyDashboardResponse getStudyDashboard(Integer userId, Integer studyId, boolean isAdmin) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디를 찾을 수 없습니다."
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if (!isAdmin && !studyMemberRepository.existsByStudyIdAndUserId(studyId, userId)) {

            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "스터디 멤버만 조회할 수 있습니다."
            );
        }

        // 1. 주간 범위 계산(KST)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate weekStart =
                today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd =
                today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 2. 스터디 회원 조회
        List<StudyMember> studyMembers = studyMemberRepository.findAllByStudyId(studyId);

        // 3. 각 회원의 정보 + 주간 달성률 계산
        List<StudyMemberResponse> memberResponses = studyMembers.stream()
                .map(studyMember -> {
                    User member = studyMember.getUser();

                    // 연동한 목표 조회
                    int achievementRate = getAchievementRate(studyMember.getId(), weekStart, weekEnd);

                    boolean isMe = userId.equals(member.getId());

                    return new StudyMemberResponse(
                            member.getId(),
                            studyMember.getId(),
                            member.getNickname(),
                            member.getProfileImageId(),
                            isMe,
                            achievementRate
                    );
                })
                .toList();

        return new StudyDashboardResponse(
                study.getId(),
                study.getStudyName(),
                study.getCategory(),
                studyMembers.size(),
                study.getMaxMembers(),
                memberResponses
        );
    }

    public StudyMemberDetailResponse getStudyMemberDetail(Integer studyId, Integer studyMemberId, Integer userId, boolean isAdmin) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디를 찾을 수 없습니다."
                ));

        StudyMember studyMember = studyMemberRepository.findById(studyMemberId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디 회원을 찾을 수 없습니다."
                ));

        if (!isAdmin && !studyMemberRepository.existsByStudyIdAndUserId(studyId, userId)) {

            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "스터디 멤버만 조회할 수 있습니다."
            );
        }

        User user = studyMember.getUser();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate weekStart =
                today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd =
                today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<StudyMemberGoal> memberGoals = studyMemberGoalRepository.findAllByStudyMemberId(studyMemberId);

        List<StudyMemberGoalResponse> goalResponses = memberGoals.stream()
                .map(memberGoal -> {
                    Goal goal = memberGoal.getGoal();

                    List<Task> tasks = taskRepository.findAllByGoalIdAndTaskDateBetween(goal.getId(), weekStart, weekEnd);
                    List<StudyMemberTaskResponse> taskResponses = tasks.stream()
                            .map(task -> new StudyMemberTaskResponse(
                                    task.getId(),
                                    task.getContent(),
                                    task.isCompleted()
                            ))
                            .toList();

                    return new StudyMemberGoalResponse(
                            goal.getId(),
                            goal.getGoalName(),
                            goal.getCategory(),
                            taskResponses
                    );
                })
                .toList();

        int achievementRate = getAchievementRate(studyMemberId, weekStart, weekEnd);

        return new StudyMemberDetailResponse(
                user.getId(),
                studyMemberId,
                user.getNickname(),
                user.getProfileImageId(),
                achievementRate,
                goalResponses
        );
    }

    // 주간 달성률 계산 메서드
    private int getAchievementRate(Integer studyMemberId, LocalDate weekStart, LocalDate weekEnd) {
        List<StudyMemberGoal> memberGoals = studyMemberGoalRepository.findAllByStudyMemberId(studyMemberId);
        List<Integer> goalIds = memberGoals.stream()
                .map(memberGoal -> memberGoal.getGoal().getId())
                .toList();

        if (goalIds.isEmpty()) {
            return 0;
        }

        List<Task> tasks = taskRepository.findAllByGoalIdInAndTaskDateBetween(goalIds, weekStart, weekEnd);

        if (tasks.isEmpty()) {
            return 0;
        }

        long totalCount = tasks.size();
        long completedCount = tasks.stream()
                .filter(Task::isCompleted)
                .count();

        return (int) (completedCount * 100 / totalCount);
    }

    public MyStudyResponse getMyStudy(Integer userId) {

        Integer studyId = studyMemberRepository.findByUserId(userId)
                .map(studyMember -> studyMember.getStudy().getId())
                .orElse(null);

        return new MyStudyResponse(studyId);
    }

    @Transactional
    public void updateStudyGoal(Integer studyId, Integer userId, StudyGoalUpdateRequest request) {

        StudyMember studyMember = studyMemberRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디 회원이 존재하지 않습니다."
                ));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "스터디가 존재하지 않습니다."
                ));

        // 기존에 연동된 목표 조회
        List<StudyMemberGoal> linkedGoals = studyMemberGoalRepository.findAllByStudyMemberId(studyMember.getId());

        Set<Integer> linkedGoalIds = linkedGoals.stream()
                .map(studyMemberGoal -> studyMemberGoal.getGoal().getId())
                .collect(Collectors.toSet());

        // 새로 선택한 목표
        Set<Integer> selectedGoalIds = new HashSet<>(request.getGoalIds());

        List<Goal> selectedGoals = goalRepository.findAllById(selectedGoalIds);

        if (selectedGoals.size() != selectedGoalIds.size()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "존재하지 않는 세부목표가 포함되어 있습니다."
            );
        }

        for (Goal goal : selectedGoals) {
            if (!goal.getUser().getId().equals(userId)) {
                throw new BusinessException(
                        HttpStatus.FORBIDDEN,
                        "본인의 세부목표만 연동할 수 있습니다."
                );
            }

            if (!goal.getCategory().equals(study.getCategory())) {
                throw new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "스터디와 같은 카테고리의 세부목표만 연동할 수 있습니다."
                );
            }
        }

        // 연동 해제
        List<StudyMemberGoal> toDelete = linkedGoals.stream()
                .filter(studyMemberGoal -> !selectedGoalIds.contains(studyMemberGoal.getGoal().getId()))
                .toList();

        studyMemberGoalRepository.deleteAll(toDelete);

        // 연동 추가
        List<StudyMemberGoal> toAdd = selectedGoals.stream()
                .filter(goal -> !linkedGoalIds.contains(goal.getId()))
                .map(goal -> new StudyMemberGoal(studyMember, goal))
                .toList();

        studyMemberGoalRepository.saveAll(toAdd);
    }
}
