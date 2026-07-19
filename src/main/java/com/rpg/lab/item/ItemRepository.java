package com.rpg.lab.item;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ItemRepository extends Repository<Item, Long> {
    Optional<Item> findById(Long id);
}
