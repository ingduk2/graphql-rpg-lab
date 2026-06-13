package com.rpg.lab.player;

public record StatsResponse(
        int attack,
        int defense,
        int speed
) {
    public static StatsResponse from(Player player) {
        return new StatsResponse(
                player.getAttack(),
                player.getDefense(),
                player.getSpeed()
        );
    }
}
