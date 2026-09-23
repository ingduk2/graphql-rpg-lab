package com.rpg.lab.inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnhancementPolicyTest {

    private final EnhancementPolicy sut = new EnhancementPolicy(
            List.of(
                    new EnhanceRate(0, 95),
                    new EnhanceRate(1, 90),
                    new EnhanceRate(2, 85),
                    new EnhanceRate(3, 80),
                    new EnhanceRate(4, 70),
                    new EnhanceRate(5, 60),
                    new EnhanceRate(6, 50),
                    new EnhanceRate(7, 40),
                    new EnhanceRate(8, 30),
                    new EnhanceRate(9, 20)
            ),
            50
    );

    @Nested
    class SuccessRateFor {

        @Test
        @DisplayName("정의된 레벨의 성공 확률을 반환한다")
        void test1() {
            assertThat(sut.successRateFor(0)).isEqualTo(95);
            assertThat(sut.successRateFor(9)).isEqualTo(20);
        }

        @Test
        @DisplayName("정의되지 않은 레벨을 조회하면 예외가 발생한다")
        void test2() {
            assertThatThrownBy(() -> sut.successRateFor(10))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class CostFor {

        @Test
        @DisplayName("레벨이 오를수록 비용이 커진다")
        void test1() {
            assertThat(sut.costFor(0)).isEqualTo(50);
            assertThat(sut.costFor(9)).isEqualTo(500);
        }
    }
}