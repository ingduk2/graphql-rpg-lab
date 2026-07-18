package com.rpg.lab.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PlayerManager {

    private final PlayerRepository playerRepository;

    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }
}
