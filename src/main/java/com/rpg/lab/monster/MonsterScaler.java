package com.rpg.lab.monster;

public class MonsterScaler {

    private final double scalingRatePerLevel;

    public MonsterScaler(double scalingRatePerLevel) {
        this.scalingRatePerLevel = scalingRatePerLevel;
    }

    public int scaleHp(Monster monster, int playerLevel) {
        return scale(monster.getMaxHp(), playerLevel);
    }

    public int scaleAttackPower(Monster monster, int playerLevel) {
        return scale(monster.getAttackPower(), playerLevel);
    }

    private int scale(int baseStat, int playerLevel) {
        double multiplier = 1 + (playerLevel - 1) * scalingRatePerLevel;
        return (int) Math.round(baseStat * multiplier);
    }
}
