package com.rpg.lab.player;

public record PlayerResponse(
        Long id,
        String name,
        int level,
        int hp,
        int maxHp,
        int exp,
        StatsResponse stats
) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getLevel(),
                player.getHp(),
                player.getMaxHp(),
                player.getExp(),
                StatsResponse.from(player)
        );
    }
}
