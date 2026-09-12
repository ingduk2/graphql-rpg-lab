package com.rpg.lab.achievement;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "achievements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    @Enumerated
    private AchievementType type;

    @Column(nullable = false)
    private int targetCount;

    public static Achievement create(
            String title,
            String description,
            AchievementType type,
            int targetCount
    ) {
        Achievement achievement = new Achievement();
        achievement.title = Objects.requireNonNull(title);
        achievement.description = description;
        achievement.type = Objects.requireNonNull(type);
        achievement.targetCount = targetCount;
        return achievement;
    }

    public boolean isAchieved(int currentCount) {
        return currentCount >= targetCount;
    }
}
