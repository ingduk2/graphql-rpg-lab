package com.rpg.lab.achievement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.rpg.lab.achievement.AchievementType.KILL_COUNT;
import static com.rpg.lab.achievement.AchievementType.LEVEL;
import static org.assertj.core.api.Assertions.assertThat;

class AchievementTest {

    @Nested
    class Create {

        @Test
        @DisplayName("업적 생성 시 최소 1개 조건이 함꼐 만들어진다")
        void test1() {
            Achievement achievement = Achievement.create("첫 걸음", "레벨 2 달성", LEVEL, 2);

            assertThat(achievement.getConditions()).hasSize(1);
        }
    }

    @Nested
    class AddCondition {

        @Test
        @DisplayName("조건을 추가로 붙이면 conditions 에 반영된다")
        void test1() {
            Achievement achievement = Achievement.create("진정한 사냥꾼", "레벨 5 + 몬스터 50마리", LEVEL, 5);

            achievement.addCondition(KILL_COUNT, 50);

            assertThat(achievement.getConditions()).hasSize(2);
        }
    }


    @Nested
    class IsAchieved {

        @Test
        @DisplayName("단일 조건이 충족되면 달성이다")
        void test1() {
            Achievement achievement = Achievement.create("첫 걸음", "레벨 2 달성", LEVEL, 2);

            boolean result = achievement.isAchieved(Map.of(LEVEL, 2));

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("단일 조건이 미충족이면 미달성이다")
        void test2() {
            Achievement achievement = Achievement.create("첫 걸음", "레벨 2 달성", LEVEL, 2);

            boolean result = achievement.isAchieved(Map.of(LEVEL, 1));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("복합 조건 중 하나라도 미충족이면 미달성이다")
        void test3() {
            Achievement achievement = Achievement.create("진정한 사냥꾼", "레벨 5 + 몬스터 50마리", LEVEL, 5)
                    .addCondition(KILL_COUNT, 50);

            boolean result = achievement.isAchieved(Map.of(
                    LEVEL, 5,
                    KILL_COUNT, 30
            ));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("복합 조건이 모두 충족되면 달성이다")
        void test4() {
            Achievement achievement = Achievement.create("진정한 사냥꾼", "레벨 5 + 몬스터 50마리", LEVEL, 5)
                    .addCondition(KILL_COUNT, 50);

            boolean result = achievement.isAchieved(Map.of(
                    LEVEL, 5,
                    KILL_COUNT, 50
            ));

            assertThat(result).isTrue();
        }
    }
}