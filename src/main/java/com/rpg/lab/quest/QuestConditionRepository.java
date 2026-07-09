package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestConditionRepository extends JpaRepository<QuestCondition, Long> {
    List<QuestCondition> findByQuestId(Long questId);
}
