package com.rpg.lab.achievement;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerAchievementRepository extends Repository<PlayerAchievement, Long> {
    PlayerAchievement save(PlayerAchievement playerAchievement);
    List<PlayerAchievement> findByPlayerId(Long playerId);

    @Query("""
        SELECT pa 
        FROM PlayerAchievement pa 
        WHERE pa.player.id = :playerId 
        AND pa.achievement.id = :achievementId
    """)
    Optional<PlayerAchievement> findByPlayerIdAndAchievementId(
            @Param("playerId") Long playerId,
            @Param("achievementId") Long achievementId
    );
}