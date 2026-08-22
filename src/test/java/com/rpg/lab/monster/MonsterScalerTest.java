package com.rpg.lab.monster;

import com.rpg.lab.fixture.MonsterFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonsterScalerTest {

    private final static double SCALING_RATE_PER_LEVEL = 0.1;

    private MonsterScaler sut;

    @BeforeEach
    void setUp() {
        sut = new MonsterScaler(SCALING_RATE_PER_LEVEL);
    }

    @Nested
    class ScaleHp {

        @Test
        @DisplayName("레벨 1이면 기본 hp 그대로다")
        void test1() {
            Monster monster = MonsterFixture.createSlime();

            ScaledMonster result = sut.scale(monster, 1);

            assertThat(result.hp()).isEqualTo(monster.getMaxHp());
        }

        @Test
        @DisplayName("레벨당 스케일링 비율만큼 hp 가 증가한다")
        void test2() {
            Monster monster = MonsterFixture.createSlime();

            ScaledMonster result = sut.scale(monster, 5);

            assertThat(result.hp()).isEqualTo(42);
        }
    }

    @Nested
    class ScaleAttackPower {

        @Test
        @DisplayName("레벨 1이면 기본 공격력 그대로다")
        void test1() {
            Monster monster = MonsterFixture.createSlime();

            ScaledMonster result = sut.scale(monster, 1);

            assertThat(result.attackPower()).isEqualTo(monster.getAttackPower());
        }

        @Test
        @DisplayName("레벨당 스케일링 비율만큼 공격력이 증가한다")
        void test2() {
            Monster monster = MonsterFixture.createSlime();

            ScaledMonster result = sut.scale(monster, 10);

            assertThat(result.attackPower()).isEqualTo(10);
        }
    }
}