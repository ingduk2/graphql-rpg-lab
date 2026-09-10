package com.rpg.lab.shop;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.inventory.InventoryResponse;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerManager;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final InventoryRepository inventoryRepository;
    private final PlayerReader playerReader;
    private final PlayerManager playerManager;

    @Transactional(readOnly = true)
    public List<ShopItemResponse> getShopItems() {
        return shopItemRepository.findAll().stream()
                .map(ShopItemResponse::from)
                .toList();
    }

    @Transactional
    public InventoryResponse buyItem(Long playerId, Long shopItemId) {
        ShopItem shopItem = shopItemRepository.findById(shopItemId)
                .orElseThrow(() -> new EntityNotFoundException("ShopItem not found: " + shopItemId));
        Player player = playerReader.getById(playerId);
        Inventory inventory = inventoryRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found, playerId: " + player));
        
        player.spendGold(shopItem.getPrice());
        inventory.addItem(shopItem.getItem());
        
        playerManager.save(player);
        inventoryRepository.save(inventory);
        
        return InventoryResponse.from(inventory);
    }
}
