package com.rpg.lab.battle;

import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.player.Player;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Battle {
    private final Player player;
    private final Monster monster;
    private final Inventory inventory;
    private final int currentMonsterHp;
    private int playerDamage;
    private int monsterDamage;
    private int monsterRemainHp;
    private int playerRemainHp;
    private boolean monsterDefeated;
    private boolean playerDefeated;
    private boolean isCritical;
    private int expGained;
    private String message;

    public Battle(
            Player player,
            Monster monster,
            Inventory inventory,
            int currentMonsterHp
    ) {
        this.player = player;
        this.monster = monster;
        this.currentMonsterHp = currentMonsterHp;
        this.inventory = inventory;
    }

    public Battle attack() {
        isCritical = ThreadLocalRandom.current().nextInt(100) < 15;

        int attackBonus = inventory != null ? inventory.getAttackBonus() : 0;
        int defenseBonus = inventory != null ? inventory.getDefenseBonus() : 0;

        playerDamage = player.getAttack() + attackBonus;
        if (isCritical) {
            playerDamage = (int) (playerDamage * 1.5);
        }

        monsterRemainHp = Math.max(0, currentMonsterHp - playerDamage);
        monsterDefeated = monsterRemainHp == 0;

        if (monsterDefeated) {
            monsterDamage = 0;
            playerRemainHp = player.getHp();
            playerDefeated = false;
            expGained = monster.getExpReward();
        } else {
            monsterDamage = Math.max(0, monster.getAttackPower() - defenseBonus);
            playerRemainHp = Math.max(0, player.getHp() - monsterDamage);
            playerDefeated = playerRemainHp == 0;
            expGained = 0;
        }

        message = buildMessage();
        return this;
    }

    private String buildMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(isCritical
                ? String.format("크리티컬! %s이(가) %s에게 %d 데미지!", player.getName(), monster.getName(), playerDamage)
                : String.format("%s이(가) %s에게 %d 데미지!", player.getName(), monster.getName(), playerDamage));

        if (monsterDefeated) {
            sb.append(String.format(" %s 처치!", monster.getName()));
        } else {
            sb.append(String.format(" %s이(가) %d 반격!", monster.getName(), monsterDamage));
            if (playerDefeated) sb.append(" 플레이어 사망...");
        }
        return sb.toString();
    }

    public Battle flee() {
        playerDamage = 0;
        monsterDamage = 0;
        monsterRemainHp = monster.getHp();
        playerRemainHp = player.getHp();
        isCritical = false;
        monsterDefeated = false;
        playerDefeated = false;
        message = player.getName() + "이(가) 도망쳤다!";
        return this;
    }
}
