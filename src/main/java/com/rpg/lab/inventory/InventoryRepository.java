package com.rpg.lab.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
                SELECT i FROM Inventory i
                LEFT JOIN FETCH i.inventoryItems ii
                LEFT JOIN FETCH ii.item
                WHERE i.player.id IN :playerIds
            """)
    List<Inventory> findAllWithItemsByPlayerIdIn(@Param("playerIds") List<Long> playerIds);

    @Query("""
                SELECT i FROM Inventory i
                LEFT JOIN FETCH i.inventoryItems ii
                LEFT JOIN FETCH ii.item
                WHERE i.player.id = :playerId
            """)
    Optional<Inventory> findWithItemsByPlayerId(@Param("playerId") Long playerId);

    Optional<Inventory> findByPlayerId(Long playerId);
}
