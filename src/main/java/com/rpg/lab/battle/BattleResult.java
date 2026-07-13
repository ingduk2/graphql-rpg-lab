package com.rpg.lab.battle;

import com.rpg.lab.item.Item;

import java.util.List;

public record BattleResult(
        int playerDamage,
        int monsterDamage,
        int monsterRemainHp,
        int playerRemainHp,
        boolean isCritical,
        boolean monsterDefeated,
        boolean playerDefeated,
        int expGained,
        int levelUps,
        DroppedItem droppedItem,
        List<String> completedQuests,
        String message
) {

    public static BattleResult from(
            Battle battle,
            int levelUps,
            Item droppedItem,
            List<String> completedQuests
    ) {
        return new BattleResult(
                battle.getPlayerDamage(),
                battle.getMonsterDamage(),
                battle.getMonsterRemainHp(),
                battle.getPlayerRemainHp(),
                battle.isCritical(),
                battle.isMonsterDefeated(),
                battle.isPlayerDefeated(),
                battle.getExpGained(),
                levelUps,
                droppedItem != null ? DroppedItem.from(droppedItem) : null,
                completedQuests,
                battle.getMessage()
        );
    }
}
