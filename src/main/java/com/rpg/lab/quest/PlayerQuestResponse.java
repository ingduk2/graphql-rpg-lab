package com.rpg.lab.quest;

public record PlayerQuestResponse(
        Long id,
        Long questId,
        QuestStatus status,
        String title,
        String description
) {
    public static PlayerQuestResponse from(PlayerQuest playerQuest) {
        return new PlayerQuestResponse(
                playerQuest.getId(),
                playerQuest.getQuest().getId(),
                playerQuest.getStatus(),
                playerQuest.getQuest().getTitle(),
                playerQuest.getQuest().getDescription()
        );
    }
}
