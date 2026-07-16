package com.rpg.lab.battle;

import com.rpg.lab.item.Item;
import com.rpg.lab.player.Player;
import com.rpg.lab.quest.PlayerQuest;
import com.rpg.lab.quest.QuestProgressUpdater;
import com.rpg.lab.quest.QuestRewardProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rpg.lab.quest.QuestType.KILL_MONSTER;
import static com.rpg.lab.quest.QuestType.LEVEL_UP;

@Component
@RequiredArgsConstructor
public class BattleVictoryProcessor {

    private final ItemDropProcessor itemDropProcessor;
    private final QuestProgressUpdater questProgressUpdater;
    private final QuestRewardProcessor questRewardProcessor;

    public BattleReward process(
            Player player,
            Battle battle,
            Long monsterId,
            Long playerId
    ) {
        if (!battle.isMonsterDefeated()) {
            return BattleReward.empty();
        }

        int levelUps = player.gainExp(battle.getExpGained());
        Item droppedItem = itemDropProcessor.process(monsterId, playerId).orElse(null);

        List<PlayerQuest> completedQuests = new ArrayList<>();
        completedQuests.addAll(questProgressUpdater.update(playerId, KILL_MONSTER, monsterId));
        if (levelUps > 0) {
            completedQuests.addAll(questProgressUpdater.update(playerId, LEVEL_UP, null));
        }

        completedQuests.forEach(quest -> questRewardProcessor.process(player, quest.getQuest().getId()));

        List<String> completedTitles = completedQuests.stream()
                .map(quest -> quest.getQuest().getTitle())
                .toList();

        return BattleReward.of(levelUps, droppedItem, completedTitles);
    }
}
