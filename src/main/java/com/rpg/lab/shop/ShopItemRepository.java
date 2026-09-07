package com.rpg.lab.shop;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ShopItemRepository extends Repository<ShopItem, Long> {
    Optional<ShopItem> findById(Long id);
    ShopItem save(ShopItem shopItem);
    List<ShopItem> findAll();
}
