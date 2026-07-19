package com.rpg.lab.monster;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonsterDropRepository extends Repository<MonsterDrop, Long> {

    @Query("""
        SELECT md FROM MonsterDrop md
        JOIN FETCH md.item
        WHERE md.monster.id = :monsterId
    """)
    List<MonsterDrop> findAllWithItemByMonsterId(@Param("monsterId") Long monsterId);
}
