package com.rpg.lab.achievement;

import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        return achievements.stream()
                .map(it -> {
                    int currentCount = currentCountFor(it, player);
                    boolean unlocked = unlockedAchievementIds.contains(it.getId());
                    return AchievementResponse.of(it, currentCount, unlocked);
                })
                .toList();
    }

    private int currentCountFor(Achievement achievement, Player player) {
        return switch (achievement.getType()) {
            case KILL_COUNT -> player.getKillCount();
            case LEVEL -> player.getLevel();
        };
    }

    @Transactional
    public List<Achievement> checkAndUnlockAchievements(Long playerId) {
        Player player = playerReader.getById(playerId);
        List<Achievement> achievements = achievementRepository.findAll();

        return achievements.stream()
                .filter(it -> !isAlreadyUnlocked(playerId, it.getId()))
                .filter(it -> isAchieved(it, player))
                .peek(it -> playerAchievementRepository.save(PlayerAchievement.create(player, it)))
                .toList();
    }

    private boolean isAlreadyUnlocked(Long playerId, Long achievementId) {
        return playerAchievementRepository.findByPlayerIdAndAchievementId(playerId, achievementId).isPresent();
    }

    private boolean isAchieved(Achievement achievement, Player player) {
        return achievement.isAchieved(currentCountFor(achievement, player));
    }
}
