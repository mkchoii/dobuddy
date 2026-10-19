package com.dobuddy.goal;

import com.dobuddy.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "goals")
@NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    @NotBlank(message = "세부목표명을 입력해주세요.")
    @Size(max = 10, message = "세부목표명은 10자 이내로 입력해주세요.")
    private String goalName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Goal(Category category, String goalName, User user) {
        this.category = category;
        this.goalName = goalName;
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateGoalName(String goalName) {
        this.goalName = goalName;
    }

}
