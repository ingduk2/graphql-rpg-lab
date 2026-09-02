package com.rpg.lab.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Nested
    class SellPrice {

        @Test
        @DisplayName("공격/방어 보너스 합산의 10배가 판매가다")
        void test1() {
            Item item = Item.create("테스트검", ItemType.WEAPON, 5, 0);

            assertThat(item.sellPrice()).isEqualTo(50);
        }

        @Test
        @DisplayName("공격+방어 둘 다 있는 아이템은 합산해서 계산된다")
        void test2() {
            Item item = Item.create("테스트반지", ItemType.ACCESSORY, 2, 2);

            assertThat(item.sellPrice()).isEqualTo(40);
        }

        @Test
        @DisplayName("공격/방어 보너스가 모두 0이면 최소 판매가 1이 보장된다")
        void test3() {
            Item item = Item.create("테스트아이템", ItemType.ACCESSORY, 0, 0);

            assertThat(item.sellPrice()).isEqualTo(1);
        }
    }
}