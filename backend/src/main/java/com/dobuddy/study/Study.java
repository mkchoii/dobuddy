package com.dobuddy.study;

import com.dobuddy.goal.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Entity
@Table(name = "studies")
@NoArgsConstructor
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 10)
    private String studyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer maxMembers = 10;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public Study(String studyName, Category category, Integer maxMembers) {
        this.studyName = studyName;
        this.category = category;
        this.maxMembers = maxMembers;
    }

    public void updateStudyName(String studyName) { this.studyName = studyName; }
    public void updateCategory(Category category) { this.category = category; }
    public void updateMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
}
