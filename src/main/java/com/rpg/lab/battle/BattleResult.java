package com.rpg.lab.battle;

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
            BattleReward battleReward
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
                battleReward.levelUps(),
                battleReward.droppedItem() != null ? DroppedItem.from(battleReward.droppedItem()) : null,
                battleReward.completedQuests(),
                battle.getMessage()
        );
    }
}
