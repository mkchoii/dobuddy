package com.dobuddy.study;

import com.dobuddy.goal.Goal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "study_member_goals", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"study_member_id", "goal_id"})
}
)
@NoArgsConstructor
public class StudyMemberGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "study_member_id", nullable = false)
    private StudyMember studyMember;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public StudyMemberGoal(StudyMember studyMember, Goal goal) {
        this.studyMember = studyMember;
        this.goal = goal;
    }

}
