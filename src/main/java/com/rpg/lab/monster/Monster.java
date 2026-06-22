package com.rpg.lab.monster;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "monsters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int hp;

    @Column(nullable = false)
    private int maxHp;

    @Column(nullable = false)
    private int attackPower;

    @Column(nullable = false)
    private int expReward;

    public static Monster create(
            String name,
            int hp,
            int attackPower,
            int expReward
    ) {
        Monster monster = new Monster();
        monster.name = Objects.requireNonNull(name);
        monster.hp = hp;
        monster.maxHp = hp;
        monster.attackPower = attackPower;
        monster.expReward = expReward;
        return monster;
    }

    public void syncHp(int hp) {
        this.hp = hp;
    }
}
