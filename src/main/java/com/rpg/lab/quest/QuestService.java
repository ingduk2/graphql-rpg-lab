package com.rpg.lab.quest;

import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final PlayerRepository playerRepository;
    private final PlayerQuestRepository playerQuestRepository;

    public List<Quest> getQuests() {
        return questRepository.findAll();
    }

    public Quest getQuest(Long id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quest not found: " + id));
    }

    @Transactional
    public PlayerQuestResponse acceptQuest(Long playerId, Long questId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));
        Quest quest = findQuestById(questId);

        PlayerQuest playerQuest = PlayerQuest.create(player, quest);
        playerQuestRepository.save(playerQuest);

        return PlayerQuestResponse.from(playerQuest);
    }

    @Transactional
    public PlayerQuestResponse completeQuest(Long playerId, Long questId) {
        PlayerQuest playerQuest = playerQuestRepository.findByPlayerIdAndQuestId(playerId, questId)
                .orElseThrow(() -> new RuntimeException("PlayerQuest not found"));

        playerQuest.complete();
        playerQuestRepository.save(playerQuest);

        return PlayerQuestResponse.from(playerQuest);
    }

    private Quest findQuestById(Long questId) {
        return questRepository.findById(questId)
                .orElseThrow(() -> new RuntimeException("Quest not found: " + questId));
    }
}
