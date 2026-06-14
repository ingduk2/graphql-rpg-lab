package com.rpg.lab.inventory;

import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public InventoryResponse equipItem(Long playerId, Long itemId) {
        Inventory inventory = inventoryRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new RuntimeException("Inventory not found, playerId: " + playerId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        inventory.addItem(item);
        inventoryRepository.save(inventory);
        return InventoryResponse.from(inventory);
    }
}
