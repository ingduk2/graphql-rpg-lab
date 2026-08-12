package com.rpg.lab.quest;

public record PrerequisiteQuestResponse(
        Long id,
        String title
) {

    public static PrerequisiteQuestResponse from(Quest quest) {
        return new PrerequisiteQuestResponse(quest.getId(), quest.getTitle());
    }
}
