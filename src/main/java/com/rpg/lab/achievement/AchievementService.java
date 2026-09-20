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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final PlayerReader playerReader;

    public List<AchievementResponse> getMyAchievements(Long playerId) {
        Player player = playerReader.getById(playerId);
        List<Achievement> achievements = achievementRepository.findAll();
        List<PlayerAchievement> unlockedList = playerAchievementRepository.findByPlayerId(playerId);
        Set<Long> unlockedAchievementIds = unlockedList.stream()
                .map(it -> it.getAchievement().getId())
                .collect(Collectors.toSet());

        Map<AchievementType, Integer> currentCounts = currentCountFor(player);

        return achievements.stream()
                .map(it -> AchievementResponse.of(
                        it,
                        currentCounts,
                        unlockedAchievementIds.contains(it.getId())
                ))
                .toList();
    }

    @Transactional
    public List<Achievement> checkAndUnlockAchievements(Long playerId) {
        Player player = playerReader.getById(playerId);
        List<Achievement> achievements = achievementRepository.findAll();
        Map<AchievementType, Integer> currentCounts = currentCountFor(player);

        return achievements.stream()
                .filter(it -> !isAlreadyUnlocked(playerId, it.getId()))
                .filter(it -> it.isAchieved(currentCounts))
                .peek(it -> playerAchievementRepository.save(PlayerAchievement.create(player, it)))
                .toList();
    }

    private Map<AchievementType, Integer> currentCountFor(Player player) {
        EnumMap<AchievementType, Integer> counts = new EnumMap<>(AchievementType.class);
        counts.put(AchievementType.KILL_COUNT, player.getKillCount());
        counts.put(AchievementType.LEVEL, player.getLevel());
        return counts;
    }

    private boolean isAlreadyUnlocked(Long playerId, Long achievementId) {
        return playerAchievementRepository.findByPlayerIdAndAchievementId(playerId, achievementId).isPresent();
    }
}
