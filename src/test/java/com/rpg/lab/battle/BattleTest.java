package com.rpg.lab.battle;

import com.rpg.lab.monster.Monster;
import com.rpg.lab.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BattleTest {

    @Nested
    class Attack {

        @Test
        @DisplayName("데미지가 플레이어 공격력 이상이다")
        void test1() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("슬라임", 30, 5, 20);

            Battle battle = new Battle(player, monster).attack();

            assertThat(battle.getPlayerDamage()).isGreaterThanOrEqualTo(player.getAttack());
        }

        @Test
        @DisplayName("몬스터 HP 가 0이하면 처리된다")
        void test2() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("슬라임", 1, 5, 20);

            Battle battle = new Battle(player, monster).attack();

            assertThat(battle.isMonsterDefeated()).isTrue();
            assertThat(battle.getMonsterRemainHp()).isEqualTo(0);
        }

        @Test
        @DisplayName("몬스터가 살아있으면 반격한다")
        void test3() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("오크", 100, 20, 80);

            Battle battle = new Battle(player, monster).attack();

            assertThat(battle.getMonsterDamage()).isEqualTo(monster.getAttackPower());
            assertThat(battle.getPlayerRemainHp()).isLessThan(player.getHp());
        }

        @Test
        @DisplayName("몬스터 처치시 반격하지 않는다")
        void test4() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("슬라임", 1, 5, 20);

            Battle battle = new Battle(player, monster).attack();

            assertThat(battle.isMonsterDefeated()).isTrue();
            assertThat(battle.getMonsterDamage()).isEqualTo(0);
            assertThat(battle.getPlayerRemainHp()).isEqualTo(player.getHp());
        }

        @Test
        @DisplayName("몬스터 처치 시 경험치를 획득한다")
        void test5() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("슬라임", 1, 5, 20);

            Battle battle = new Battle(player, monster).attack();

            assertThat(battle.isMonsterDefeated()).isTrue();
            assertThat(battle.getExpGained()).isEqualTo(20);
        }

        @Test
        @DisplayName("몬스터가 살아있으면 경험치를 획득하지 않는다")
        void test6() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("오크", 100, 20, 80);

            Battle battle = new Battle(player, monster).attack();

            assertThat(battle.isMonsterDefeated()).isFalse();
            assertThat(battle.getExpGained()).isEqualTo(0);
        }
    }

    @Nested
    class Flee {

        @Test
        @DisplayName("데미지가 0이고 플레이어 HP 가 유지된다")
        void test1() {
            Player player = Player.create("user1");
            Monster monster = Monster.create("슬라임", 30, 5, 20);

            Battle battle = new Battle(player, monster).flee();

            assertThat(battle.getPlayerDamage()).isEqualTo(0);
            assertThat(battle.isMonsterDefeated()).isFalse();
            assertThat(battle.getPlayerRemainHp()).isEqualTo(player.getHp());
        }
    }
}