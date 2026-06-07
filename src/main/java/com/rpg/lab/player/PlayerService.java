package com.rpg.lab.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayer(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found: " + id));
    }
}
