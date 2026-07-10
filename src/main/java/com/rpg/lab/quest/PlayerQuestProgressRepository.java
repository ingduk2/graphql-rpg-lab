package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerQuestProgressRepository extends JpaRepository<PlayerQuestProgress, Long> {

    @Query("""
            SELECT p FROM PlayerQuestProgress p
            JOIN FETCH p.questCondition
            WHERE p.playerQuest.id = :playerQuestId
            """)
    List<PlayerQuestProgress> findAllWithConditionByPlayerQuestId(@Param("playerQuestId") Long playerQuestId);
}
