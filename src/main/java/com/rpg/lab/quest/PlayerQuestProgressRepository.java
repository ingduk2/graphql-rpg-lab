package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerQuestProgressRepository extends Repository<PlayerQuestProgress, Long> {

    PlayerQuestProgress save(PlayerQuestProgress playerQuestProgress);

    @Query("""
            SELECT p FROM PlayerQuestProgress p
            JOIN FETCH p.questCondition
            WHERE p.playerQuest.id = :playerQuestId
            """)
    List<PlayerQuestProgress> findProgressByPlayerQuestId(@Param("playerQuestId") Long playerQuestId);
}
