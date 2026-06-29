package com.rpg.lab.battle;

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
        boolean finished
) {
}
