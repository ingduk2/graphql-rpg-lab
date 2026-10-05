package com.rpg.lab.inventory;

import com.rpg.lab.item.ItemResponse;
import com.rpg.lab.item.ItemSetType;

import java.util.List;

public record InventoryResponse(
        Long id,
        List<ItemResponse> items,
        List<ItemSetType> activeSets
) {
    public static InventoryResponse from(Inventory inventory) {
        List<ItemResponse> items = inventory.getInventoryItems().stream()
                .map(it -> ItemResponse.from(
                        it.getItem(),
                        it.getEnhancedAttackBonus(),
                        it.getEnhancedDefenseBonus(),
                        it.isEquipped(),
                        it.getEnhanceLevel())
                )
                .toList();

        return new InventoryResponse(
                inventory.getId(),
                items,
                inventory.getActiveSets()
        );
    }
}
