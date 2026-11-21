package com.rpg.lab.inventory;

import com.rpg.lab.common.RandomProvider;
import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.exception.InvalidItemOperationException;
import com.rpg.lab.exception.MaxEnhanceLevelException;
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
import java.util.Optional;
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

    public InventoryItem addItem(Item item) {
        if (item.isConsumable()) {
            Optional<InventoryItem> owned = findOptionalByItemId(item.getId());
            if (owned.isPresent()) {
                owned.get().increaseQuantity();
                return owned.get();
            }
        }

        InventoryItem created = InventoryItem.create(this, item);
        inventoryItems.add(created);
        return created;
    }

    public int sellItem(Long inventoryItemId) {
        InventoryItem target = getInventoryItem(inventoryItemId);
        int price = target.getItem().sellPrice();
        removeOne(target);
        return price;
    }

    public void addItemIfNotOwned(Item item) {
        if (item.isConsumable() || !hasItem(item.getId())) {
            addItem(item);
        }
    }

    public boolean hasItem(Long itemId) {
        return inventoryItems.stream()
                .anyMatch(it -> it.getItem().getId().equals(itemId));
    }

    public void equip(Long inventoryItemId) {
        InventoryItem target = getInventoryItem(inventoryItemId);

        if (target.getItem().isConsumable()) {
            throw new InvalidItemOperationException("Consumable cannot be equipped: " + inventoryItemId);
        }

        inventoryItems.stream()
                .filter(it ->
                        it != target &&
                        it.isEquipped() &&
                        it.getItem().getType() == target.getItem().getType()
                )
                .forEach(InventoryItem::unequip);

        target.equip();
    }

    public void unequip(Long inventoryItemId) {
        getInventoryItem(inventoryItemId).unequip();
    }


    private InventoryItem getInventoryItem(Long inventoryItemId) {
        return inventoryItems.stream()
                .filter(it -> inventoryItemId.equals(it.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not owned: " + inventoryItemId));
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
            Long inventoryItemId,
            EnhancementPolicy policy,
            RandomProvider randomProvider
    ) {
        InventoryItem target = getInventoryItem(inventoryItemId);

        if (target.getItem().isConsumable()) {
            throw new InvalidItemOperationException("Consumable cannot be enhanced: " + inventoryItemId);
        }

        if (target.isMaxEnhanceLevel()) {
            throw new MaxEnhanceLevelException("Already at max enhance level: " + inventoryItemId);
        }

        int successRate = policy.successRateFor(target.getEnhanceLevel());
        boolean success = randomProvider.nextInt(100) < successRate;
        target.applyEnhanceResult(success);

        return new EnhanceResult(success, target.getEnhanceLevel());
    }

    public int getEnhanceLevelOf(Long inventoryItemId) {
        return getInventoryItem(inventoryItemId).getEnhanceLevel();
    }

    public List<ItemSetType> getActiveSets() {
        return equippedSetCounts().entrySet().stream()
                .filter(it -> it.getValue() >= SET_BONUS_THRESHOLD)
                .map(Map.Entry::getKey)
                .toList();
    }

    public int useItem(Long inventoryItemId) {
        InventoryItem target = getInventoryItem(inventoryItemId);
        if (!target.getItem().isConsumable()) {
            throw new InvalidItemOperationException("Not a consumable item: " + inventoryItemId);
        }

        removeOne(target);
        return target.getItem().getHealAmount();
    }

    private Map<ItemSetType, Long> equippedSetCounts() {
        return inventoryItems.stream()
                .filter(InventoryItem::isEquipped)
                .map(InventoryItem::getItem)
                .filter(Item::belongsToSet)
                .collect(Collectors.groupingBy(Item::getSetType, Collectors.counting()));
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

    private Optional<InventoryItem> findOptionalByItemId(Long itemId) {
        return inventoryItems.stream()
                .filter(it -> it.getItem().getId().equals(itemId))
                .findFirst();
    }

    private void removeOne(InventoryItem target) {
        if (target.decreaseQuantity()) {
            inventoryItems.remove(target);
        }
    }
}
