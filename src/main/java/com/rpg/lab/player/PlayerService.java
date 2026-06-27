package com.rpg.lab.player;

import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    public List<PlayerResponse> getPlayers() {
        return playerRepository.findAll().stream()
                .map(PlayerResponse::from)
                .toList();
    }

    public PlayerResponse getPlayer(Long id) {
        return playerRepository.findById(id)
                .map(PlayerResponse::from)
                .orElseThrow(() -> new RuntimeException("Player not found: " + id));
    }

    @Transactional
    public PlayerResponse createPlayer(String name) {
        Player savedPlayer = playerRepository.save(Player.create(name));
        inventoryRepository.save(Inventory.create(savedPlayer));
        return PlayerResponse.from(savedPlayer);
    }
}
