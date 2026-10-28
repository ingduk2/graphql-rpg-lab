package com.rpg.lab.achievement;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PlayerAchievementRepository extends Repository<PlayerAchievement, Long> {
    PlayerAchievement save(PlayerAchievement playerAchievement);
    List<PlayerAchievement> findByPlayerId(Long playerId);

    @Query("""
        SELECT pa.achievement.id
        FROM PlayerAchievement pa
        WHERE pa.player.id = :playerId
    """)
    Set<Long> findAchievementIdsByPlayerId(@Param("playerId") Long playerId);
}