package com.rpg.lab.achievement;

public record AchievementConditionResponse(
        AchievementType type,
        int targetCount,
        int currentCount
) {
    public static AchievementConditionResponse of(
            AchievementCondition condition,
            int currentCount
    ) {
        return new AchievementConditionResponse(
                condition.getType(),
                condition.getTargetCount(),
                currentCount
        );
    }
}
