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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemSetType setType;

    public static Item create(
            String name,
            ItemType type,
            int attackBonus,
            int defenseBonus
    ) {
        return create(name, type, attackBonus, defenseBonus, ItemSetType.NONE);
    }

    public static Item create(
            String name,
            ItemType type,
            int attackBonus,
            int defenseBonus,
            ItemSetType setType
    ) {
        Item item = new Item();
        item.name = Objects.requireNonNull(name);
        item.type = Objects.requireNonNull(type);
        item.attackBonus = attackBonus;
        item.defenseBonus = defenseBonus;
        item.setType = Objects.requireNonNull(setType);
        return item;
    }

    public int sellPrice() {
        return Math.max(1, (attackBonus + defenseBonus) * 10);
    }

    public boolean belongsToSet() {
        return setType != ItemSetType.NONE;
    }
}
