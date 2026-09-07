package com.rpg.lab.shop;

import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShopItemTest {

    @Nested
    class Create {

        @Test
        @DisplayName("정상 가격으로 생성된다")
        void test1() {
            Item item = ItemFixture.createSwordItem();

            ShopItem result = ShopItem.create(item, 100);

            assertThat(result.getPrice()).isEqualTo(100);
            assertThat(result.getItem()).isEqualTo(item);
        }

        @Test
        @DisplayName("가격이 0 이하면 IllegalArgumentException 이 발생한다")
        void test2() {
            Item item = ItemFixture.createSwordItem();

            assertThatThrownBy(() -> ShopItem.create(item, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격이 음수면 IllegalArgumentException 이 발생한다")
        void test3() {
            Item item = ItemFixture.createSwordItem();

            assertThatThrownBy(() -> ShopItem.create(item, -10))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}