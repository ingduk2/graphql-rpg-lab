package com.rpg.lab.quest;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final PlayerRepository playerRepository;
    private final PlayerQuestRepository playerQuestRepository;
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

        if (quest.hasPrerequisite()) {
            Long prerequisiteQuestId = quest.getPrerequisiteQuest().getId();
            boolean completed = playerQuestRepository.findByPlayerIdAndQuestId(playerId, prerequisiteQuestId)
                    .map(pq -> pq.getStatus() == QuestStatus.COMPLETED)
                    .orElse(false);
            quest.validatePrerequisiteCompleted(completed);
        }

        PlayerQuest savedPlayerQuest = playerQuestRepository.save(PlayerQuest.start(player, quest));

        List<PlayerQuestProgress> progressList = playerQuestProgressRepository.findProgressByPlayerQuestId(savedPlayerQuest.getId());

        return PlayerQuestResponse.from(savedPlayerQuest, progressList);
    }

    @Transactional
    public PlayerQuestResponse completeQuest(Long playerId, Long questId) {
        PlayerQuest playerQuest = playerQuestRepository.findByPlayerIdAndQuestId(playerId, questId)
                .orElseThrow(() -> new EntityNotFoundException("PlayerQuest not found"));

        playerQuest.complete();
        playerQuestRepository.save(playerQuest);

        List<PlayerQuestProgress> progressList = playerQuestProgressRepository.findProgressByPlayerQuestId(playerQuest.getId());

        return PlayerQuestResponse.from(playerQuest, progressList);
    }

    @Transactional(readOnly = true)
    public List<PlayerQuestResponse> getMyQuests(Long playerId) {
        List<PlayerQuest> playerQuests = playerQuestRepository.findAllWithQuestByPlayerId(playerId);
        if (playerQuests.isEmpty()) {
            return List.of();
        }

        List<Long> playerQuestIds = playerQuests.stream()
                .map(PlayerQuest::getId)
                .toList();

        Map<Long, List<PlayerQuestProgress>> progressByPlayerQuestId = playerQuestProgressRepository
                .findProgressByPlayerQuestIdIn(playerQuestIds).stream()
                .collect(Collectors.groupingBy(p -> p.getPlayerQuest().getId()));

        return playerQuests.stream()
                .map(pq -> PlayerQuestResponse.from(
                        pq,
                        progressByPlayerQuestId.getOrDefault(pq.getId(), List.of())
                ))
                .toList();
    }

    private Quest findQuestById(Long questId) {
        return questRepository.findById(questId)
                .orElseThrow(() -> new EntityNotFoundException("Quest not found: " + questId));
    }
}
