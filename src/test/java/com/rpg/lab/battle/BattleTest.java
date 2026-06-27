package com.rpg.lab.battle;

import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemType;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BattleTest {

    private Player player;
    private Inventory inventory;
    private Monster slime;
    private Monster orc;

    @BeforeEach
    void setUp() {
        player = Player.create("user1");
        inventory = Inventory.create(player);
        slime = Monster.create("슬라임", 30, 5, 20);
        orc = Monster.create("오크", 100, 20, 80);
    }

    @Nested
    class Attack {

        @Test
        @DisplayName("데미지가 플레이어 공격력 이상이다")
        void test1() {
            Battle battle = attack(slime);

            assertThat(battle.getPlayerDamage()).isGreaterThanOrEqualTo(player.getAttack());
        }

        @Test
        @DisplayName("몬스터 HP 가 0이하면 처리된다")
        void test2() {
            Battle battle = attackWithHp(slime, 1);

            assertThat(battle.isMonsterDefeated()).isTrue();
            assertThat(battle.getMonsterRemainHp()).isEqualTo(0);
        }

        @Test
        @DisplayName("몬스터가 살아있으면 반격한다")
        void test3() {
            Battle battle = attack(orc);

            assertThat(battle.getMonsterDamage()).isEqualTo(orc.getAttackPower());
            assertThat(battle.getPlayerRemainHp()).isLessThan(player.getHp());
        }

        @Test
        @DisplayName("몬스터 처치시 반격하지 않는다")
        void test4() {
            Battle battle = attackWithHp(slime, 1);

            assertThat(battle.isMonsterDefeated()).isTrue();
            assertThat(battle.getMonsterDamage()).isEqualTo(0);
            assertThat(battle.getPlayerRemainHp()).isEqualTo(player.getHp());
        }

        @Test
        @DisplayName("몬스터 처치 시 경험치를 획득한다")
        void test5() {
            Battle battle = attackWithHp(slime, 1);

            assertThat(battle.isMonsterDefeated()).isTrue();
            assertThat(battle.getExpGained()).isEqualTo(20);
        }

        @Test
        @DisplayName("몬스터가 살아있으면 경험치를 획득하지 않는다")
        void test6() {
            Battle battle = attack(orc);

            assertThat(battle.isMonsterDefeated()).isFalse();
            assertThat(battle.getExpGained()).isEqualTo(0);
        }
    }

    @Nested
    class AttackWithItems {

        @Test
        @DisplayName("공격 아이템 보너스가 데미지에 반영된다")
        void test1() {
            Item item = Item.create("테스트 검", ItemType.WEAPON, 10, 0);
            inventory.addItem(item);

            Battle battle = attack(orc);

            assertThat(battle.getPlayerDamage()).isGreaterThanOrEqualTo(player.getAttack() + 10);
        }

        @Test
        @DisplayName("방어 아이템 보너스가 몬스터 반격 데미지에 반영된다")
        void test2() {
            Item item = Item.create("테스트 갑옷", ItemType.ARMOR, 0, 5);
            inventory.addItem(item);

            Battle battle = attack(orc);

            assertThat(battle.getMonsterDamage()).isEqualTo(orc.getAttackPower() - 5);
        }

        @Test
        @DisplayName("액세서리 보너스가 공격과 방어 둘 다 반영한다")
        void test3() {
            Item item = Item.create("테스트 반지", ItemType.ACCESSORY, 2, 2);
            inventory.addItem(item);

            Battle battle = attack(orc);

            assertThat(battle.getPlayerDamage()).isGreaterThanOrEqualTo(player.getAttack() + 2);
            assertThat(battle.getMonsterDamage()).isEqualTo(orc.getAttackPower() - 2);
        }
    }

    @Nested
    class Flee {

        @Test
        @DisplayName("데미지가 0이고 플레이어 HP 가 유지된다")
        void test1() {
            Battle battle = flee(slime);

            assertThat(battle.getPlayerDamage()).isEqualTo(0);
            assertThat(battle.isMonsterDefeated()).isFalse();
            assertThat(battle.getPlayerRemainHp()).isEqualTo(player.getHp());
        }
    }

    private Battle attack(Monster monster) {
        return new Battle(player, monster, inventory, monster.getHp()).attack();
    }

    private Battle attackWithHp(Monster monster, int monsterHp) {
        return new Battle(player, monster, inventory, monsterHp).attack();
    }

    private Battle flee(Monster monster) {
        return new Battle(player, monster, inventory, monster.getHp()).flee();
    }
}