package com.rpg.lab.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

    @Nested
    class GainExp {

        @Test
        @DisplayName("레벨업 기준치 미만이면 레벨이 유지된다")
        void test1() {
            Player player = Player.create("user1");

            player.gainExp(50);

            assertThat(player.getLevel()).isEqualTo(1);
            assertThat(player.getExp()).isEqualTo(50);
        }

        @Test
        @DisplayName("기준치 이상이면 레벨업하고 초과 경험치는 이월된다")
        void test2() {
            Player player = Player.create("user1");

            player.gainExp(150);

            assertThat(player.getLevel()).isEqualTo(2);
            assertThat(player.getExp()).isEqualTo(50);
        }

        @Test
        @DisplayName("레벨업 시 스탯이 상승하고 HP가 풀피로 회복된")
        void test3() {
            Player player = Player.create("user1");
            int beforeAttack = player.getAttack();

            player.gainExp(100);

            assertThat(player.getAttack()).isEqualTo(beforeAttack + PlayerDefaults.LEVEL_UP_ATTACK_BONUS);
            assertThat(player.getHp()).isEqualTo(player.getMaxHp());
        }

        @Test
        @DisplayName("연속 레벨업이 가능하다")
        void test4() {
            Player player = Player.create("user1");

            int levelUps = player.gainExp(350);

            assertThat(levelUps).isEqualTo(2);
            assertThat(player.getLevel()).isEqualTo(3);
        }
    }

    @Nested
    class IncreaseKillCount {

        @Test
        @DisplayName("처치 시 killCount 가 1 증가한다")
        void test1() {
            Player player = Player.create("user1");

            player.increaseKillCount();

            assertThat(player.getKillCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("여러 번 처치하면 그만큼 누적된다")
        void test2() {
            Player player = Player.create("user1");

            int killCount = 10;
            for (int i = 0; i < killCount; i++) {
                player.increaseKillCount();
            }

            assertThat(player.getKillCount()).isEqualTo(killCount);
        }
    }
}