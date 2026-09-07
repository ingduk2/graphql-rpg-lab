package com.rpg.lab.shop;

import com.rpg.lab.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "shop_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private int price;

    public static ShopItem create(Item item, int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("price must be positive: " + price);
        }

        ShopItem shopItem = new ShopItem();
        shopItem.item = Objects.requireNonNull(item);
        shopItem.price = price;
        return shopItem;
    }
}
