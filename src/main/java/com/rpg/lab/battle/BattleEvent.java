package com.rpg.lab.battle;

import java.util.List;

public record BattleEvent(
        String battleId,
        int turn,
        int playerDamage,
        int monsterDamage,
        int monsterRemainHp,
        int playerRemainHp,
        boolean isCritical,
        boolean monsterDefeated,
        boolean playerDefeated,
        String message,
        boolean finished,
        int levelUps,
        DroppedItem droppedItem,
        List<String> completedQuests
) {
    public static BattleEvent of(
            String battleId,
            int turn,
            Battle battle,
            BattleReward battleReward,
            boolean finished
    ) {
        return new BattleEvent(
                battleId, turn,
                battle.getPlayerDamage(), battle.getMonsterDamage(),
                battle.getMonsterRemainHp(), battle.getPlayerRemainHp(),
                battle.isCritical(), battle.isMonsterDefeated(),
                battle.isPlayerDefeated(), battle.getMessage(),
                finished,
                battleReward.levelUps(),
                battleReward.droppedItem() != null ? DroppedItem.from(battleReward.droppedItem()) : null,
                battleReward.completedQuests()
        );
    }
}
