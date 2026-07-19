package com.rpg.lab.monster;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MonsterRepository extends Repository<Monster, Long> {
    Optional<Monster> findById(Long id);
    Monster save(Monster monster);
    List<Monster> findAll();
}
