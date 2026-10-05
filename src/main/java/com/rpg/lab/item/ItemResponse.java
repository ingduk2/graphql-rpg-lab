package com.rpg.lab.item;

public record ItemResponse(
        Long id,
        String name,
        ItemType type,
        int attackBonus,
        int defenseBonus,
        boolean equipped,
        int enhanceLevel,
        ItemSetType setType
) {
    public static ItemResponse from(
            Item item,
            int attackBonus,
            int defenseBonus,
            boolean equipped,
            int enhanceLevel) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getType(),
                attackBonus,
                defenseBonus,
                equipped,
                enhanceLevel,
                item.getSetType()
        );
    }
}
