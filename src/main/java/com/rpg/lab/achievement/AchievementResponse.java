package com.rpg.lab.achievement;

public record AchievementResponse(
        Long id,
        String title,
        String description,
        AchievementType type,
        int targetCount,
        boolean unlocked,
        int currentCount
) {
    public static AchievementResponse of(
            Achievement achievement,
            int currentCount,
            boolean unlocked
    ) {
        return new AchievementResponse(
                achievement.getId(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.getType(),
                achievement.getTargetCount(),
                unlocked,
                currentCount
        );
    }
}
