package com.rpg.lab.player;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends Repository<Player, Long> {
    Optional<Player> findById(Long id);
    Player save(Player player);
    List<Player> findAll();
}
