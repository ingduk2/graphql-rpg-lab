package com.rpg.lab.inventory;

import com.rpg.lab.item.Item;
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

    @Column(nullable = false)
    private boolean equipped;

    public static InventoryItem create(Inventory inventory, Item item) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.inventory = inventory;
        inventoryItem.item = item;
        inventoryItem.equipped = false;
        return inventoryItem;
    }

    void equip() {
        this.equipped = true;
    }

    void unequip() {
        this.equipped = false;
    }
}
