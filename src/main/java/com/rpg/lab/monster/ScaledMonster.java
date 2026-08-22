package com.rpg.lab.monster;

public record ScaledMonster(
        String name,
        int hp,
        int attackPower,
        int expReward
) {
    public static ScaledMonster from(Monster monster, int hp, int attackPower) {
        return new ScaledMonster(
                monster.getName(),
                hp,
                attackPower,
                monster.getExpReward()
        );
    }
}
