package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryReader {

    private final InventoryRepository inventoryRepository;

    public Inventory getWithItemsByPlayerId(Long playerId) {
        return inventoryRepository.findWithItemsByPlayerId(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found: " + playerId));
    }
}
