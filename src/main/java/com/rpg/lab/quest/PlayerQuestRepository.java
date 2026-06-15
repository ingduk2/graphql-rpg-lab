package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerQuestRepository extends JpaRepository<PlayerQuest, Long> {
    Optional<PlayerQuest> findByPlayerIdAndQuestId(Long playerId, Long questId);
}
