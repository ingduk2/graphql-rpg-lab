package com.rpg.lab.player;

public record LeaderboardEntry(
        int rank,
        Long playerId,
        String playerName,
        int level,
        int exp
) {
    public static LeaderboardEntry of(int rank, Player player) {
        return new LeaderboardEntry(rank, player.getId(), player.getName(), player.getLevel(), player.getExp());
    }
}
