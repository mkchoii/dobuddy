package com.dobuddy.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyMemberGoalRepository extends JpaRepository<StudyMemberGoal, Integer> {

    List<StudyMemberGoal> findAllByStudyMemberId(Integer studyMemberId);
    void deleteAllByStudyMemberId(Integer studyMemberId);
    @Modifying
    @Query("""
        DELETE FROM StudyMemberGoal smg
        WHERE smg.goal.id IN (
            SELECT g.id FROM Goal g WHERE g.user.id = :userId
        )
        OR smg.studyMember.id IN (
            SELECT sm.id FROM StudyMember sm WHERE sm.user.id = :userId
        )
    """)
    void deleteAllByUserId(@Param("userId") Integer userId);
    boolean existsByGoalId(Integer goalId);
    StudyMemberGoal findByGoalId(Integer goalId);
    List<StudyMemberGoal> findAllByStudyMemberIdOrderByGoal_CreatedAtAsc(
            Integer studyMemberId
    );
}
