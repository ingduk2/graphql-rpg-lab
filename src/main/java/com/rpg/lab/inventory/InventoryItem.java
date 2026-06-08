package com.rpg.lab.inventory;

import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static InventoryItem create(Inventory inventory, Item item) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.inventory = inventory;
        inventoryItem.item = item;
        return inventoryItem;
    }
}
