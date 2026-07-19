package com.rpg.lab.quest;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface QuestConditionRepository extends Repository<QuestCondition, Long> {
    List<QuestCondition> findByQuestId(Long questId);
}
