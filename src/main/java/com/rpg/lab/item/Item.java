package com.rpg.lab.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = false)
    private int attackBonus;

    @Column(nullable = false)
    private int defenseBonus;

    public static Item create(
            String name,
            ItemType type,
            int attackBonus,
            int defenseBonus
    ) {
        Item item = new Item();
        item.name = Objects.requireNonNull(name);
        item.type = Objects.requireNonNull(type);
        item.attackBonus = attackBonus;
        item.defenseBonus = defenseBonus;
        return item;
    }
}
