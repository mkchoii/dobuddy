package com.dobuddy.user;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 15, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer profileImageId;

    @Column(length = 15)
    private String finalGoal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime accessAt;

    @Column
    private LocalDateTime firstGoalCreatedAt;

    @Column
    private LocalDateTime firstTaskCreatedAt;

    @Column
    private LocalDateTime firstTaskCompletedAt;

    @Column
    private LocalDateTime firstStudyJoinedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public User(
            String nickname,
            String email,
            String password,
            Integer profileImageId
    ) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageId = profileImageId;
        this.role = Role.USER;
    }

    public void updateFinalGoal(String finalGoal) { this.finalGoal = finalGoal; }

    public void updateProfileImage(Integer profileImageId) { this.profileImageId = profileImageId; }

    public void updatePassword(String password) { this.password = password; }
    public void updateAccessAt(LocalDateTime accessAt) { this.accessAt = accessAt; }
    public void setFirstGoalCreatedAt(LocalDateTime firstGoalCreatedAt) { this.firstGoalCreatedAt = firstGoalCreatedAt; }
    public void setFirstTaskCreatedAt(LocalDateTime firstTaskCreatedAt) { this.firstTaskCreatedAt = firstTaskCreatedAt; }
    public void setFirstTaskCompletedAt(LocalDateTime firstTaskCompletedAt) { this.firstTaskCompletedAt = firstTaskCompletedAt; }
    public void setFirstStudyJoinedAt(LocalDateTime firstStudyJoinedAt) { this.firstStudyJoinedAt = firstStudyJoinedAt; }
}
