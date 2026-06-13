package com.rpg.lab.player;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int hp;

    @Column(nullable = false)
    private int maxHp;

    // Stats
    @Column(nullable = false)
    private int attack;

    @Column(nullable = false)
    private int defense;

    @Column(nullable = false)
    private int speed;

    public static Player create(String name) {
        Player player = new Player();
        player.name = Objects.requireNonNull(name);
        player.level = PlayerDefaults.LEVEL;
        player.hp = PlayerDefaults.HP;
        player.maxHp = PlayerDefaults.MAX_HP;
        player.attack = PlayerDefaults.ATTACK;
        player.defense = PlayerDefaults.DEFENSE;
        player.speed = PlayerDefaults.SPEED;
        return player;
    }
}
