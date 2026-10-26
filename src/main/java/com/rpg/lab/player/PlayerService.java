package com.rpg.lab.player;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private static final int MAX_LEADERBOARD_LIMIT = 100;

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
                .orElseThrow(() -> new EntityNotFoundException("Player not found: " + id));
    }

    public List<LeaderboardEntry> getLeaderboard(int limit, LeaderboardSortBy sortBy) {
        Limit pageLimit = Limit.of(Math.clamp(limit, 1, MAX_LEADERBOARD_LIMIT));

        List<Player> players = switch (sortBy) {
            case LEVEL -> playerRepository.findAllByOrderByLevelDescExpDesc(pageLimit);
            case KILL_COUNT -> playerRepository.findAllByOrderByKillCountDesc(pageLimit);
        };

        return IntStream.range(0, Math.min(limit, players.size()))
                .mapToObj(i -> LeaderboardEntry.of(i + 1, players.get(i)))
                .toList();
    }

    @Transactional
    public PlayerResponse createPlayer(String name) {
        Player savedPlayer = playerRepository.save(Player.create(name));
        inventoryRepository.save(Inventory.create(savedPlayer));
        return PlayerResponse.from(savedPlayer);
    }
}
