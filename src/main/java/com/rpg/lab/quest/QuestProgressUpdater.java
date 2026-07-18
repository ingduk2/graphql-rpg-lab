package com.rpg.lab.quest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        List<PlayerQuest> completedQuests = new ArrayList<>(update(playerId, KILL_MONSTER, monsterId));
        if (levelUps > 0) {
            completedQuests.addAll(update(playerId, LEVEL_UP, null));
        }

        return completedQuests;
    }

    private List<PlayerQuest> update(
            Long playerId,
            QuestType questType,
            Long monsterId
    ) {
        List<PlayerQuest> completedQuests = new ArrayList<>();

        List<PlayerQuest> inProgressQuests = playerQuestRepository.findByPlayerIdAndStatus(playerId, IN_PROGRESS);

        for (PlayerQuest playerQuest : inProgressQuests) {
            List<PlayerQuestProgress> progressList = playerQuestProgressRepository.findProgressByPlayerQuestId(playerQuest.getId());

            progressList.stream()
                    .filter(p -> p.getQuestCondition().getType() == questType)
                    .filter(p -> {
                        Long targetMonsterId = p.getQuestCondition().getTargetMonsterId();
                        return targetMonsterId == null || targetMonsterId.equals(monsterId);
                    })
                    .forEach(p -> {
                        p.increment();
                        playerQuestProgressRepository.save(p);
                    });

            boolean allCompleted = progressList.stream().allMatch(PlayerQuestProgress::isCompleted);
            if (allCompleted && !progressList.isEmpty()) {
                playerQuest.complete();
                playerQuestRepository.save(playerQuest);
                completedQuests.add(playerQuest);
            }
        }

        return completedQuests;
    }
}
