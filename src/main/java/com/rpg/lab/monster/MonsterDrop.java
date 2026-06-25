package com.rpg.lab.monster;

import com.rpg.lab.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monster_drops")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonsterDrop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id")
    private Monster monster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private double dropRate;

    public static MonsterDrop create(
            Monster monster,
            Item item,
            double dropRate
    ) {
        MonsterDrop monsterDrop = new MonsterDrop();
        monsterDrop.monster = monster;
        monsterDrop.item = item;
        monsterDrop.dropRate = dropRate;
        return monsterDrop;
    }
}
