package com.rpg.lab.achievement;

import java.util.List;
import java.util.Map;

public record AchievementResponse(
        Long id,
        String title,
        String description,
        boolean unlocked,
        List<AchievementConditionResponse> conditions
) {
    public static AchievementResponse of(
            Achievement achievement,
            Map<AchievementType, Integer> currentCounts,
            boolean unlocked
    ) {
        List<AchievementConditionResponse> conditionResponses = achievement.getConditions().stream()
                .map(it -> AchievementConditionResponse.of(
                        it,
                        currentCounts.getOrDefault(it.getType(), 0)
                ))
                .toList();

        return new AchievementResponse(
                achievement.getId(),
                achievement.getTitle(),
                achievement.getDescription(),
                unlocked,
                conditionResponses
        );
    }
}
