package com.rpg.lab.fixture;

import com.rpg.lab.player.Player;

public class PlayerFixture {

    public static Player create() {
        return Player.create("테스터");
    }
}
