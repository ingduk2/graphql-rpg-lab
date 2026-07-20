package com.rpg.lab.fixture;

import com.rpg.lab.monster.Monster;

public class MonsterFixture {

    public static Monster createSlime() {
        return Monster.create("슬라임", 30, 5, 20);
    }

    public static Monster createOrc() {
        return Monster.create("오크", 100, 20, 80);
    }
}
