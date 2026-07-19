package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerQuestRepository extends Repository<PlayerQuest, Long> {

    PlayerQuest save(PlayerQuest playerQuest);

    Optional<PlayerQuest> findByPlayerIdAndQuestId(Long playerId, Long questId);

    @Query("""
            SELECT pq FROM PlayerQuest pq
            JOIN FETCH pq.quest
            WHERE pq.player.id = :playerId AND pq.status = :status
            """)
    List<PlayerQuest> findByPlayerIdAndStatus(@Param("playerId") Long playerId, @Param("status") QuestStatus status);

    List<PlayerQuest> findByPlayerId(Long playerId);
}
