package com.rpg.lab.monster;

public record MonsterResponse(
        Long id,
        String name,
        int hp,
        int maxHp,
        int attackPower
) {
    public static MonsterResponse from(Monster monster) {
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                monster.getHp(),
                monster.getMaxHp(),
                monster.getAttackPower()
        );
    }

    public static MonsterResponse from(Monster monster, ScaledMonster scaledMonster) {
        return new MonsterResponse(
                monster.getId(),
                scaledMonster.name(),
                scaledMonster.hp(),
                scaledMonster.hp(),
                scaledMonster.attackPower()
        );
    }
}
