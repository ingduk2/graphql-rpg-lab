package com.rpg.lab.achievement;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AchievementCondition> conditions = new ArrayList<>();

    public static Achievement create(
            String title,
            String description,
            AchievementType type,
            int targetCount
    ) {
        Achievement achievement = new Achievement();
        achievement.title = Objects.requireNonNull(title);
        achievement.description = description;
        achievement.conditions.add(AchievementCondition.create(achievement, type, targetCount));
        return achievement;
    }

    public Achievement addCondition(AchievementType type, int targetCount) {
        conditions.add(AchievementCondition.create(this, type, targetCount));
        return this;
    }

    public boolean isAchieved(Map<AchievementType, Integer> currentCounts) {
        return conditions.stream()
                .allMatch(it -> it.isAchieved(currentCounts.getOrDefault(it.getType(), 0)));
    }
}
