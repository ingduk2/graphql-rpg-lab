package com.rpg.lab.item;

public record ItemResponse(
        Long id,
        String name,
        ItemType type,
        int attackBonus,
        int defenseBonus
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getType(),
                item.getAttackBonus(),
                item.getDefenseBonus()
        );
    }
}
