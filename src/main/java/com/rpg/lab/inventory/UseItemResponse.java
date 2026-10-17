package com.rpg.lab.inventory;

public record UseItemResponse(
        int healedAmount,
        InventoryResponse inventor
) {
    public static UseItemResponse of(int healedAmount, Inventory inventory) {
        return new UseItemResponse(
                healedAmount,
                InventoryResponse.from(inventory)
        );
    }
}
