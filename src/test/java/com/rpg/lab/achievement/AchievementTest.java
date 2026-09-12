package com.rpg.lab.achievement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AchievementTest {

    @Nested
    class IsAchieved {

        @Test
        @DisplayName("현재 카운트가 목표치보다 작으면 미달성이다")
        void test1() {
            Achievement achievement = Achievement.create("몬스터 사냥꾼", "몬스터 10마리 처치", AchievementType.KILL_COUNT, 10);

            assertThat(achievement.isAchieved(5)).isFalse();
        }

        @Test
        @DisplayName("현재 카운트가 목표치와 같으면 달성이다")
        void test2() {
            Achievement achievement = Achievement.create("몬스터 사냥꾼", "몬스터 10마리 처치", AchievementType.KILL_COUNT, 10);

            assertThat(achievement.isAchieved(10)).isTrue();
        }

        @Test
        @DisplayName("현재 카운트가 목표치보다 크면 달성이다")
        void test3() {
            Achievement achievement = Achievement.create("몬스터 사냥꾼", "몬스터 10마리 처치", AchievementType.KILL_COUNT, 10);

            assertThat(achievement.isAchieved(15)).isTrue();
        }
    }
}