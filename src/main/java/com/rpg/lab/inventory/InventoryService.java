package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerManager;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PlayerReader playerReader;
    private final PlayerManager playerManager;

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

    @Transactional
    public InventoryResponse sellItem(Long playerId, Long itemId) {
        Inventory inventory = findInventoryByPlayerId(playerId);
        Player player = playerReader.getById(playerId);

        int price = inventory.sellItem(itemId);
        player.gainGold(price);

        inventoryRepository.save(inventory);
        playerManager.save(player);

        return InventoryResponse.from(inventory);
    }

    private @NonNull Inventory findInventoryByPlayerId(Long playerId) {
        return inventoryRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found, playerId: " + playerId));
    }
}
