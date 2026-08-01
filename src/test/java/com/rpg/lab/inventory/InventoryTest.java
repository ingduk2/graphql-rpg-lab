package com.rpg.lab.inventory;

import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.item.Item;
import com.rpg.lab.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = PlayerFixture.create();
    }

    @Nested
    class AddItem {

        @Test
        @DisplayName("아이템을 추가하면 inventoryItems 에 반영된다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item item = ItemFixture.createSwordItemWithId();

            inventory.addItem(item);

            assertThat(inventory.getInventoryItems())
                    .extracting(InventoryItem::getItem)
                    .containsExactly(item);
        }

        @Test
        @DisplayName("같은 아이템을 여러 번 addItem 하면 중복으로 다 쌓인다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item item = ItemFixture.createSwordItemWithId();

            inventory.addItem(item);
            inventory.addItem(item);

            assertThat(inventory.getInventoryItems())
                    .hasSize(2)
                    .extracting(InventoryItem::getItem)
                    .containsExactly(item, item);
        }
    }

    @Nested
    class AddItemIfNotOwned {

        @Test
        @DisplayName("보유하지 않은 아이템이면 추가된다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            inventory.addItem(swordItem);
            Item shieldItem = ItemFixture.createShieldItemWithId();

            inventory.addItemIfNotOwned(shieldItem);

            assertThat(inventory.getInventoryItems())
                    .extracting(InventoryItem::getItem)
                    .containsExactlyInAnyOrder(swordItem, shieldItem);
        }

        @Test
        @DisplayName("이미 보유한 아이템이면 추가되지 않는다 (개수 그대로)")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            inventory.addItem(swordItem);

            inventory.addItemIfNotOwned(swordItem);

            assertThat(inventory.getInventoryItems())
                    .hasSize(1)
                    .extracting(InventoryItem::getItem)
                    .containsExactly(swordItem);
        }
    }

    @Nested
    class RemoveItem {

        @Test
        @DisplayName("보유 중인 아이템을 제거하면 목록에서 사라진다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item item = ItemFixture.createSwordItemWithId();
            inventory.addItem(item);

            inventory.removeItem(item.getId());

            assertThat(inventory.getInventoryItems()).isEmpty();
        }

        @Test
        @DisplayName("보유하지 않은 itemId 로 제거를 시도해도 예외 없이 그냥 무시된다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Long invalidItemId = 999L;

            inventory.removeItem(invalidItemId);

            assertThat(inventory.getInventoryItems()).isEmpty();
        }
    }

    @Nested
    class AttackBonus {

        @Test
        @DisplayName("아이템이 없으면 0 을 반환한다")
        void test1() {
            Inventory inventory = Inventory.create(player);

            int attackBonus = inventory.getAttackBonus();

            assertThat(attackBonus).isEqualTo(0);
        }

        @Test
        @DisplayName("여러 아이템의 공격 보너스가 합산된다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            Item axeItem = ItemFixture.createAxeItemWithId();
            inventory.addItem(swordItem);
            inventory.addItem(axeItem);

            int attackBonus = inventory.getAttackBonus();

            assertThat(attackBonus).isEqualTo(swordItem.getAttackBonus() + axeItem.getAttackBonus());
        }
    }

    @Nested
    class DefenseBonus {

        @Test
        @DisplayName("아이템이 없으면 0 을 반환한다")
        void test1() {
            Inventory inventory = Inventory.create(player);

            int defenseBonus = inventory.getDefenseBonus();

            assertThat(defenseBonus).isEqualTo(0);
        }

        @Test
        @DisplayName("여러 아이템의 방어 보너스가 합산된다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item shieldItem = ItemFixture.createShieldItemWithId();
            Item armorItem = ItemFixture.createArmorItemWithId();
            inventory.addItem(shieldItem);
            inventory.addItem(armorItem);

            int defenseBonus = inventory.getDefenseBonus();

            assertThat(defenseBonus).isEqualTo(shieldItem.getDefenseBonus() + armorItem.getDefenseBonus());
        }
    }

}