package com.rpg.lab.battle;

public record BattleResult(
        int playerDamage,
        int monsterDamage,
        int monsterRemainHp,
        int playerRemainHp,
        boolean isCritical,
        boolean monsterDefeated,
        boolean playerDefeated,
        int expGained,
        String message
) {

    public static BattleResult from(Battle battle) {
        return new BattleResult(
                battle.getPlayerDamage(),
                battle.getMonsterDamage(),
                battle.getMonsterRemainHp(),
                battle.getPlayerRemainHp(),
                battle.isCritical(),
                battle.isMonsterDefeated(),
                battle.isPlayerDefeated(),
                battle.getExpGained(),
                battle.getMessage()
        );
    }
}
