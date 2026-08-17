package com.rpg.lab.item;

public record ItemResponse(
        Long id,
        String name,
        ItemType type,
        int attackBonus,
        int defenseBonus,
        boolean equipped
) {
    public static ItemResponse from(Item item, boolean equipped) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getType(),
                item.getAttackBonus(),
                item.getDefenseBonus(),
                equipped
        );
    }
}
