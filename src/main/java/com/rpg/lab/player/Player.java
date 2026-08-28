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

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private int killCount;

    public static Player create(String name) {
        Player player = new Player();
        player.name = Objects.requireNonNull(name);
        player.level = PlayerDefaults.LEVEL;
        player.hp = PlayerDefaults.HP;
        player.maxHp = PlayerDefaults.MAX_HP;
        player.attack = PlayerDefaults.ATTACK;
        player.defense = PlayerDefaults.DEFENSE;
        player.speed = PlayerDefaults.SPEED;
        player.exp = PlayerDefaults.EXP;
        player.killCount = 0;
        return player;
    }

    public void syncHp(int hp) {
        this.hp = hp;
    }

    public void increaseKillCount() {
        this.killCount++;
    }

    public int gainExp(int amount) {
        this.exp += amount;
        int levelUps = 0;
        while (this.exp >= expToNextLevel()) {
            this.exp -= expToNextLevel();
            levelUp();
            levelUps++;
        }
        return levelUps;
    }

    private int expToNextLevel() {
        return this.level * PlayerDefaults.EXP_PER_LEVEL;
    }

    private void levelUp() {
        this.level += 1;
        this.maxHp += PlayerDefaults.LEVEL_UP_HP_BONUS;
        this.hp = this.maxHp;
        this.attack += PlayerDefaults.LEVEL_UP_ATTACK_BONUS;
        this.defense += PlayerDefaults.LEVEL_UP_DEFENSE_BONUS;
        this.speed += PlayerDefaults.LEVEL_UP_SPEED_BONUS;
    }
}
