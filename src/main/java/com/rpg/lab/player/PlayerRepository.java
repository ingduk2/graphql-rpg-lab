package com.rpg.lab.player;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends Repository<Player, Long> {
    Optional<Player> findById(Long id);
    Player save(Player player);
    List<Player> findAll();
    void deleteById(Long id);

    @Query("SELECT p FROM Player p ORDER BY p.level DESC, p.exp DESC")
    List<Player> findAllOrderByLevelDescExpDesc();

    @Query("SELECT p FROM Player p ORDER BY p.killCount DESC")
    List<Player> findAllOrderByKillCountDesc();
}
