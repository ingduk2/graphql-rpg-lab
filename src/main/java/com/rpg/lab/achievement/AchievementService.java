package com.rpg.lab.achievement;

import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final PlayerReader playerReader;

    @Transactional(readOnly = true)
    public List<AchievementResponse> getMyAchievements(Long playerId) {
        Player player = playerReader.getById(playerId);
        List<Achievement> achievements = achievementRepository.findAllWithConditions();
        Set<Long> unlockedIds = playerAchievementRepository.findAchievementIdsByPlayerId(playerId);
        Map<AchievementType, Integer> currentCounts = currentCountFor(player);

        return achievements.stream()
                .map(it -> AchievementResponse.of(it, currentCounts, unlockedIds.contains(it.getId())))
                .toList();
    }

    @Transactional
    public List<Achievement> checkAndUnlockAchievements(Long playerId) {
        Player player = playerReader.getById(playerId);
        Set<Long> unlockedIds = playerAchievementRepository.findAchievementIdsByPlayerId(playerId);
        Map<AchievementType, Integer> currentCounts = currentCountFor(player);

        List<Achievement> newlyUnlocked = achievementRepository.findAllWithConditions().stream()
                .filter(it -> !unlockedIds.contains(it.getId()))
                .filter(it -> it.isAchieved(currentCounts))
                .toList();

        newlyUnlocked.forEach(it -> playerAchievementRepository.save(PlayerAchievement.create(player, it)));
        return newlyUnlocked;
    }

    private Map<AchievementType, Integer> currentCountFor(Player player) {
        EnumMap<AchievementType, Integer> counts = new EnumMap<>(AchievementType.class);
        counts.put(AchievementType.KILL_COUNT, player.getKillCount());
        counts.put(AchievementType.LEVEL, player.getLevel());
        return counts;
    }
}
