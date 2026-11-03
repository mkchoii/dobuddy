package com.dobuddy.study;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Integer> {

    long countByStudyId(Integer studyId);

    boolean existsByStudyIdAndUserId(
            Integer studyId,
            Integer userId
    );

    List<StudyMember> findAllByStudyId(Integer studyId);

    Optional<StudyMember> findByIdAndStudyId(Integer studyMemberId, Integer studyId);
    void deleteAllByUserId(Integer userId);
    Optional<StudyMember> findByUserId(Integer userId);
    boolean existsByUserId(Integer userId);
    Optional<StudyMember> findByStudyIdAndUserId(
            Integer StudyId,
            Integer UserId
    );
    void deleteAllByStudyId(Integer studyId);
}
