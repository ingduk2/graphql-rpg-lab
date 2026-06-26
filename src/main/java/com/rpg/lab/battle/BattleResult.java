package com.rpg.lab.battle;

import com.rpg.lab.item.Item;

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
        String message
) {

    public static BattleResult from(
            Battle battle,
            int levelUps,
            Item droppedItem
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
                battle.getMessage()
        );
    }
}
