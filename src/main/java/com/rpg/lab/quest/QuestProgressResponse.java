package com.rpg.lab.quest;

public record QuestProgressResponse(
        String type,
        int currentCount,
        int targetCount
) {
    public static QuestProgressResponse from(PlayerQuestProgress progress) {
        return new QuestProgressResponse(
                progress.getQuestCondition().getType().name(),
                progress.getCurrentCount(),
                progress.getQuestCondition().getTargetCount()
        );
    }
}
