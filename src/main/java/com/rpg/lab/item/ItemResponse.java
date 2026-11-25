package com.rpg.lab.item;

public record ItemResponse(
        Long id,
        Long inventoryItemId,
        String name,
        ItemType type,
        int attackBonus,
        int defenseBonus,
        boolean equipped,
        int enhanceLevel,
        ItemSetType setType,
        int healAmount,
        int quantity
) {
    public static ItemResponse from(
            Item item,
            Long inventoryItemId,
            int attackBonus,
            int defenseBonus,
            boolean equipped,
            int enhanceLevel,
            int quantity
    ) {
        return new ItemResponse(
                item.getId(),
                inventoryItemId,
                item.getName(),
                item.getType(),
                attackBonus,
                defenseBonus,
                equipped,
                enhanceLevel,
                item.getSetType(),
                item.getHealAmount(),
                quantity
        );
    }
}
