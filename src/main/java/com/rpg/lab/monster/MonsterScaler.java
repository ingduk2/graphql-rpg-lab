package com.rpg.lab.monster;

public class MonsterScaler {

    private final double scalingRatePerLevel;

    public MonsterScaler(double scalingRatePerLevel) {
        this.scalingRatePerLevel = scalingRatePerLevel;
    }

    public ScaledMonster scale(Monster monster, int playerLevel) {
        int hp = scale(monster.getMaxHp(), playerLevel);
        int attackPower = scale(monster.getAttackPower(), playerLevel);
        return ScaledMonster.from(monster, hp, attackPower);
    }

    private int scale(int baseStat, int playerLevel) {
        double multiplier = 1 + (playerLevel - 1) * scalingRatePerLevel;
        return (int) Math.round(baseStat * multiplier);
    }
}
