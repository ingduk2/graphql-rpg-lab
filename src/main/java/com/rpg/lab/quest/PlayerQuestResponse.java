package com.rpg.lab.quest;

import java.util.List;

public record PlayerQuestResponse(
        Long id,
        Long questId,
        QuestStatus status,
        String title,
        String description,
        List<QuestProgressResponse> progress
) {
    public static PlayerQuestResponse from(
            PlayerQuest playerQuest,
            List<PlayerQuestProgress> progressList
    ) {
        return new PlayerQuestResponse(
                playerQuest.getId(),
                playerQuest.getQuest().getId(),
                playerQuest.getStatus(),
                playerQuest.getQuest().getTitle(),
                playerQuest.getQuest().getDescription(),
                progressList.stream()
                        .map(QuestProgressResponse::from)
                        .toList()
        );
    }
}
