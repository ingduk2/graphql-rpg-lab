package com.rpg.lab.battle;

import com.rpg.lab.item.Item;
import com.rpg.lab.player.Player;
import com.rpg.lab.quest.PlayerQuest;
import com.rpg.lab.quest.QuestProgressUpdater;
import com.rpg.lab.quest.QuestRewardProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BattleVictoryProcessor {

    private final ItemDropProcessor itemDropProcessor;
    private final QuestProgressUpdater questProgressUpdater;
    private final QuestRewardProcessor questRewardProcessor;

    public BattleReward process(
            Player player,
            Battle battle,
            Long monsterId
    ) {
        // 전투 HP 동기화
        player.syncHp(battle.getPlayerRemainHp());

        if (!battle.isMonsterDefeated()) {
            return BattleReward.empty();
        }

        // 전투 경험치 획득 및 레벨업
        int levelUps = player.gainExp(battle.getExpGained());

        // 전투 아이템 드롭
        Item droppedItem = itemDropProcessor.process(monsterId, player.getId()).orElse(null);

        // 퀘스트 진행도 업데이트
        List<PlayerQuest> completedQuests = questProgressUpdater.updateOnBattleVictory(player.getId(), monsterId, levelUps);

        // 퀘스트 완료 보상 지급
        completedQuests.forEach(quest -> questRewardProcessor.process(player, quest.getQuest().getId()));

        List<String> completedTitles = getCompletedTitles(completedQuests);
        return BattleReward.of(levelUps, droppedItem, completedTitles);
    }

    private static List<String> getCompletedTitles(List<PlayerQuest> completedQuests) {
        return completedQuests.stream()
                .map(quest -> quest.getQuest().getTitle())
                .toList();
    }
}
