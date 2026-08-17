package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse equipItem(Long playerId, Long itemId) {
        Inventory inventory = findInventoryByPlayerId(playerId);

        inventory.equip(itemId);
        inventoryRepository.save(inventory);
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse unequipItem(Long playerId, Long itemId) {
        Inventory inventory = findInventoryByPlayerId(playerId);

        inventory.unequip(itemId);
        inventoryRepository.save(inventory);
        return InventoryResponse.from(inventory);
    }

    private @NonNull Inventory findInventoryByPlayerId(Long playerId) {
        return inventoryRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found, playerId: " + playerId));
    }
}
