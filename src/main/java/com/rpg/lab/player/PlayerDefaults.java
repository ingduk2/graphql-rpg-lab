package com.rpg.lab.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerDefaults {

    public static final int LEVEL = 1;
    public static final int HP = 100;
    public static final int MAX_HP = 100;
    public static final int ATTACK = 10;
    public static final int DEFENSE = 5;
    public static final int SPEED = 5;
    public static final int EXP = 0;

    public static final int LEVEL_UP_HP_BONUS = 10;
    public static final int LEVEL_UP_ATTACK_BONUS = 2;
    public static final int LEVEL_UP_DEFENSE_BONUS = 1;
    public static final int LEVEL_UP_SPEED_BONUS = 1;
    public static final int EXP_PER_LEVEL = 100;
}
