package com.rpg.lab.battle;

import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.item.Item;
import com.rpg.lab.monster.MonsterDrop;
import com.rpg.lab.monster.MonsterDropRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class ItemDropProcessor {

    private final MonsterDropRepository monsterDropRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public Optional<Item> process(Long monsterId, Long playerId) {
        List<MonsterDrop> drops = monsterDropRepository.findAllWithItemByMonsterId(monsterId);

        Optional<Item> droppedItem = calculateDrop(drops);

        droppedItem.ifPresent(item -> equipIfNotOwned(item, playerId));

        return droppedItem;
    }

    private Optional<Item> calculateDrop(List<MonsterDrop> drops) {
        return drops.stream()
                .filter(drop -> ThreadLocalRandom.current().nextDouble() < drop.getDropRate())
                .map(MonsterDrop::getItem)
                .findFirst();
    }

    private void equipIfNotOwned(Item item, Long playerId) {
        Inventory inventory = inventoryRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.addItemIfNotOwned(item);
        inventoryRepository.save(inventory);
    }
}
