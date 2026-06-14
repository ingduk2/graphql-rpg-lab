package com.rpg.lab.inventory;

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

    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
}
