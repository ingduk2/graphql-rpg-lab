package com.rpg.lab.achievement;

import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final PlayerReader playerReader;

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
        int currentCount = switch (achievement.getType()) {
            case KILL_COUNT -> player.getKillCount();
            case LEVEL -> player.getLevel();
        };
        return achievement.isAchieved(currentCount);
    }
}
