package com.rpg.lab.shop;

import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemType;

public record ShopItemResponse(
        Long id,
        Long itemId,
        String itemName,
        ItemType itemType,
        int attackBonus,
        int defenseBonus,
        int price
) {
    public static ShopItemResponse from(ShopItem shopItem) {
        Item item = shopItem.getItem();
        return new ShopItemResponse(
                shopItem.getId(),
                item.getId(),
                item.getName(),
                item.getType(),
                item.getAttackBonus(),
                item.getDefenseBonus(),
                shopItem.getPrice()
        );
    }
}
