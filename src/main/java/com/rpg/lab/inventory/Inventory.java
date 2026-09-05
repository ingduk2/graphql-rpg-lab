package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.item.Item;
import com.rpg.lab.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    public static Inventory create(Player player) {
        Inventory inventory = new Inventory();
        inventory.player = player;
        return inventory;
    }

    public void addItem(Item item) {
        InventoryItem inventoryItem = InventoryItem.create(this, item);
        this.inventoryItems.add(inventoryItem);
    }

    public void removeItem(Long itemId) {
        this.inventoryItems.removeIf(li -> li.getItem().getId().equals(itemId));
    }

    public int sellItem(Long itemId) {
        InventoryItem target = findByItemId(itemId);
        int price = target.getItem().sellPrice();
        inventoryItems.remove(target);
        return price;
    }

    public void addItemIfNotOwned(Item item) {
        if (!hasItem(item.getId())) {
            addItem(item);
        }
    }

    private boolean hasItem(Long itemId) {
        return inventoryItems.stream()
                .anyMatch(it -> it.getItem().getId().equals(itemId));
    }

    public void equip(Long itemId) {
        InventoryItem target = findByItemId(itemId);

        inventoryItems.stream()
                .filter(it -> it.isEquipped() && it.getItem().getType() == target.getItem().getType())
                .forEach(InventoryItem::unequip);

        target.equip();
    }

    public void unequip(Long itemId) {
        findByItemId(itemId).unequip();
    }

    private InventoryItem findByItemId(Long itemId) {
        return inventoryItems.stream()
                .filter(it -> it.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item not owned: " + itemId));
    }

    public int getAttackBonus() {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .mapToInt(it -> it.getItem().getAttackBonus())
                .sum();
    }

    public int getDefenseBonus() {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .mapToInt(it -> it.getItem().getDefenseBonus())
                .sum();
    }
}
