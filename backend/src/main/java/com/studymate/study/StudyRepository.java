package com.studymate.study;

import com.studymate.goal.Category;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Integer> {

    List<Study> findAllByCategory(Category category);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Study s WHERE s.id = :studyId")
    Optional<Study> findByIdForUpdate(@Param("studyId") Integer studyId);
}
