package com.rpg.lab.quest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rpg.lab.quest.QuestStatus.IN_PROGRESS;
import static com.rpg.lab.quest.QuestType.KILL_MONSTER;
import static com.rpg.lab.quest.QuestType.LEVEL_UP;

@Component
@RequiredArgsConstructor
public class QuestProgressUpdater {

    private final PlayerQuestRepository playerQuestRepository;
    private final PlayerQuestProgressRepository playerQuestProgressRepository;

    @Transactional
    public List<PlayerQuest> updateOnBattleVictory(
            Long playerId,
            Long monsterId,
            int levelUps
    ) {
        List<PlayerQuest> inProgressQuests = playerQuestRepository.findByPlayerIdAndStatus(playerId, IN_PROGRESS);
        if (inProgressQuests.isEmpty()) {
            return List.of();
        }

        Map<Long, List<PlayerQuestProgress>> progressByPlayerQuestId = loadProgress(inProgressQuests);

        List<PlayerQuest> completedQuests = update(inProgressQuests, progressByPlayerQuestId, KILL_MONSTER, monsterId);

        if (levelUps > 0) {
            completedQuests.addAll(update(inProgressQuests, progressByPlayerQuestId, LEVEL_UP, null));
        }

        return completedQuests;
    }

    private Map<Long, List<PlayerQuestProgress>> loadProgress(List<PlayerQuest> playerQuests) {
        List<Long> playerQuestIds = playerQuests.stream()
                .map(PlayerQuest::getId)
                .toList();

        return playerQuestProgressRepository.findProgressByPlayerQuestIdIn(playerQuestIds).stream()
                .collect(Collectors.groupingBy(p -> p.getPlayerQuest().getId()));
    }

    private List<PlayerQuest> update(
            List<PlayerQuest> playerQuests,
            Map<Long, List<PlayerQuestProgress>> progressByPlayerQuestId,
            QuestType questType,
            Long monsterId
    ) {
        List<PlayerQuest> completedQuests = new ArrayList<>();

        for (PlayerQuest playerQuest : playerQuests) {
            if (playerQuest.getStatus() != IN_PROGRESS) {
                continue;
            }

            List<PlayerQuestProgress> progressList = progressByPlayerQuestId.getOrDefault(playerQuest.getId(), List.of());

            progressList.stream()
                    .filter(p -> p.getQuestCondition().getType() == questType)
                    .filter(p -> {
                        Long targetMonsterId = p.getQuestCondition().getTargetMonsterId();
                        return targetMonsterId == null || targetMonsterId.equals(monsterId);
                    })
                    .forEach(PlayerQuestProgress::increment);

            boolean allCompleted = progressList.stream().allMatch(PlayerQuestProgress::isCompleted);
            if (allCompleted && !progressList.isEmpty()) {
                playerQuest.complete();
                completedQuests.add(playerQuest);
            }
        }

        return completedQuests;
    }
}
