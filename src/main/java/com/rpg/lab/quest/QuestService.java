package com.rpg.lab.quest;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rpg.lab.quest.QuestStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final PlayerRepository playerRepository;
    private final PlayerQuestRepository playerQuestRepository;
    private final QuestConditionRepository questConditionRepository;
    private final PlayerQuestProgressRepository playerQuestProgressRepository;

    public List<Quest> getQuests() {
        return questRepository.findAll();
    }

    public Quest getQuest(Long id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quest not found: " + id));
    }

    @Transactional
    public PlayerQuestResponse acceptQuest(Long playerId, Long questId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found: " + playerId));
        Quest quest = findQuestById(questId);

        PlayerQuest savedPlayerQuest = playerQuestRepository.save(PlayerQuest.create(player, quest));

        // 퀘스트 조건별 진행도 생성
        List<QuestCondition> conditions = questConditionRepository.findByQuestId(questId);
        for (QuestCondition condition : conditions) {
            playerQuestProgressRepository.save(PlayerQuestProgress.create(savedPlayerQuest, condition));
        }

        return PlayerQuestResponse.from(savedPlayerQuest);
    }

    @Transactional
    public PlayerQuestResponse completeQuest(Long playerId, Long questId) {
        PlayerQuest playerQuest = playerQuestRepository.findByPlayerIdAndQuestId(playerId, questId)
                .orElseThrow(() -> new EntityNotFoundException("PlayerQuest not found"));

        playerQuest.complete();
        playerQuestRepository.save(playerQuest);

        return PlayerQuestResponse.from(playerQuest);
    }

    @Transactional
    public void updateProgress(Long playerId, QuestType questType) {
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
            }
        }
    }

    private Quest findQuestById(Long questId) {
        return questRepository.findById(questId)
                .orElseThrow(() -> new EntityNotFoundException("Quest not found: " + questId));
    }
}
