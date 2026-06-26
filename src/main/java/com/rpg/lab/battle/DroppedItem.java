package com.rpg.lab.battle;

import com.rpg.lab.item.Item;

public record DroppedItem(
        Long id,
        String name,
        String type
) {
    public static DroppedItem from(Item item) {
        return new DroppedItem(
                item.getId(),
                item.getName(),
                item.getType().name()
        );
    }
}
