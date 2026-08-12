package com.rpg.lab.quest;

public record QuestResponse(
        Long id,
        String title,
        String description,
        PrerequisiteQuestResponse prerequisiteQuest
) {
    public static QuestResponse from(Quest quest) {
        return new QuestResponse(
                quest.getId(),
                quest.getTitle(),
                quest.getDescription(),
                quest.hasPrerequisite() ?
                        PrerequisiteQuestResponse.from(quest.getPrerequisiteQuest()) :
                        null
        );
    }
}
