package com.rpg.lab.inventory;

import com.rpg.lab.item.ItemResponse;

import java.util.List;

public record InventoryResponse(
        Long id,
        Long playerId,
        List<ItemResponse> items
) {
    public static InventoryResponse from(Inventory inventory) {
        List<ItemResponse> items = inventory.getInventoryItems().stream()
                .map(inventoryItem -> ItemResponse.from(inventoryItem.getItem()))
                .toList();

        return new InventoryResponse(
                inventory.getId(),
                inventory.getPlayer().getId(),
                items
        );
    }
}
