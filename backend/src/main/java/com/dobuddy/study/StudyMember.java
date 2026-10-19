package com.dobuddy.study;

import com.dobuddy.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "study_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"study_id", "user_id"})
}
)
@NoArgsConstructor
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }

    public StudyMember(Study study, User user) {
        this.study = study;
        this.user = user;
    }
}
