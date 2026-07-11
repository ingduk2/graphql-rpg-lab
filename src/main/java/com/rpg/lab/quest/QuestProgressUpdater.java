package com.rpg.lab.quest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rpg.lab.quest.QuestStatus.IN_PROGRESS;

@Component
@RequiredArgsConstructor
public class QuestProgressUpdater {

    private final PlayerQuestRepository playerQuestRepository;
    private final PlayerQuestProgressRepository playerQuestProgressRepository;
    private final QuestRewardProcessor questRewardProcessor;

    @Transactional
    public void update(Long playerId, QuestType questType) {
        List<PlayerQuest> inProgressQuests = playerQuestRepository.findByPlayerIdAndStatus(playerId, IN_PROGRESS);

        for (PlayerQuest playerQuest : inProgressQuests) {
            List<PlayerQuestProgress> progressList = playerQuestProgressRepository
                    .findAllWithConditionByPlayerQuestId(playerQuest.getId());

            progressList.stream()
                    .filter(p -> p.getQuestCondition().getType() == questType)
                    .forEach(p -> {
                        p.increment();
                        playerQuestProgressRepository.save(p);
                    });

            boolean allCompleted = progressList.stream().allMatch(PlayerQuestProgress::isCompleted);
            if (allCompleted && !progressList.isEmpty()) {
                playerQuest.complete();
                playerQuestRepository.save(playerQuest);
                questRewardProcessor.process(playerQuest.getPlayer(), playerQuest.getQuest().getId());
            }
        }
    }
}
