package com.rpg.lab.inventory;

public record EnhanceItemResponse(
        boolean success,
        int newEnhanceLevel,
        InventoryResponse inventory
) {
    public static EnhanceItemResponse of(
            EnhanceResult enhanceResult,
            Inventory inventory
    ) {
        return new EnhanceItemResponse(
                enhanceResult.success(),
                enhanceResult.newEnhanceLevel(),
                InventoryResponse.from(inventory)
        );
    }
}
