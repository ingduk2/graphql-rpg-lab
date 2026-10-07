package com.rpg.lab.inventory;

import com.rpg.lab.common.RandomProvider;
import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemSetType;
import com.rpg.lab.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    static final int SET_BONUS_THRESHOLD = 2;
    static final int SET_BONUS_PERCENT = 100;

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
        return equippedBonus(InventoryItem::getEnhancedAttackBonus)
                + setBonus(InventoryItem::getEnhancedAttackBonus);
    }

    public int getDefenseBonus() {
        return equippedBonus(InventoryItem::getEnhancedDefenseBonus)
                + setBonus(InventoryItem::getEnhancedDefenseBonus);
    }

    public EnhanceResult enhanceItem(
            Long itemId,
            EnhancementPolicy policy,
            RandomProvider randomProvider
    ) {
        InventoryItem target = findByItemId(itemId);

        if (target.isMaxEnhanceLevel()) {
            throw new IllegalStateException("Already at max enhance level");
        }

        int successRate = policy.successRateFor(target.getEnhanceLevel());
        boolean success = randomProvider.nextInt(100) < successRate;
        target.applyEnhanceResult(success);

        return new EnhanceResult(success, target.getEnhanceLevel());
    }

    public int getEnhanceLevelOf(Long itemId) {
        return findByItemId(itemId).getEnhanceLevel();
    }

    public List<ItemSetType> getActiveSets() {
        return equippedSetCounts().entrySet().stream()
                .filter(it -> it.getValue() >= SET_BONUS_THRESHOLD)
                .map(Map.Entry::getKey)
                .toList();
    }

    private Map<ItemSetType, Long> equippedSetCounts() {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .map(InventoryItem::getItem)
                .filter(Item::belongsToSet)
                .collect(Collectors.groupingBy(Item::getSetType, Collectors.counting()));
    }

    private int activeSetCount() {
        return (int) equippedSetCounts().values().stream()
                .filter(count -> count >= SET_BONUS_THRESHOLD)
                .count();
    }

    private int equippedBonus(ToIntFunction<InventoryItem> statOf) {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .mapToInt(statOf)
                .sum();
    }

    // 세트가 발동 중일 때, 그 세트 아이템들의 스탯 합 × N% (올림)
    private int setBonus(ToIntFunction<InventoryItem> statOf) {
        List<ItemSetType> activeSets = getActiveSets();
        int base = inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .filter(it -> activeSets.contains(it.getItem().getSetType()))
                .mapToInt(statOf)
                .sum();
        return (base * SET_BONUS_PERCENT + 99) / 100;
    }
}
