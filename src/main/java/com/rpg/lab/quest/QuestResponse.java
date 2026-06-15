package com.rpg.lab.quest;

public record QuestResponse(
        Long id,
        String title,
        String description
) {
    public static QuestResponse from(Quest quest) {
        return new QuestResponse(
                quest.getId(),
                quest.getTitle(),
                quest.getDescription()
        );
    }
}
