package com.rpg.lab.player;

import com.rpg.lab.inventory.InventoryResponse;

public record PlayerResponse(
        Long id,
        String name,
        int level,
        int hp,
        int maxHp,
        int attack,
        int defence,
        int speed,
        InventoryResponse inventory
) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getLevel(),
                player.getHp(),
                player.getMaxHp(),
                player.getAttack(),
                player.getDefence(),
                player.getSpeed(),
                player.getInventory() != null
                        ? InventoryResponse.from(player.getInventory())
                        : null
        );
    }
}
