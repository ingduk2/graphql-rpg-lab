package com.rpg.lab.fixture;

import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.ScaledMonster;

public class MonsterFixture {

    public static Monster createSlime() {
        return Monster.create("슬라임", 30, 5, 20);
    }

    public static Monster createOrc() {
        return Monster.create("오크", 100, 20, 80);
    }

    public static Monster createBoss() {
        return Monster.create("보스", 1, 1, 9999);
    }

    public static ScaledMonster unscaled(Monster monster) {
        return ScaledMonster.from(monster, monster.getHp(), monster.getAttackPower());
    }
}
