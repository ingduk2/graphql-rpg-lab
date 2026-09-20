package com.rpg.lab.achievement;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "achievement_conditions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AchievementCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AchievementType type;

    @Column(nullable = false)
    private int targetCount;

    static AchievementCondition create(
            Achievement achievement,
            AchievementType type,
            int targetCount
    ) {
        AchievementCondition achievementCondition = new AchievementCondition();
        achievementCondition.achievement = Objects.requireNonNull(achievement);
        achievementCondition.type = Objects.requireNonNull(type);
        achievementCondition.targetCount = targetCount;
        return achievementCondition;
    }

    boolean isAchieved(int currentCount) {
        return currentCount >= targetCount;
    }
}
