package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.item.Item;
import com.rpg.lab.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
        @DisplayName("보유만 하고 장착하지 않으면 반영되지 않는다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            inventory.addItem(swordItem);

            assertThat(inventory.getAttackBonus()).isEqualTo(0);
        }

        @Test
        @DisplayName("서로 다른 타입의 장착 아이템의 공격 보너스가 합산된다")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            Item ringItem = ItemFixture.createRingItemWithId();
            inventory.addItem(swordItem);
            inventory.addItem(ringItem);
            inventory.equip(swordItem.getId());
            inventory.equip(ringItem.getId());

            int attackBonus = inventory.getAttackBonus();

            assertThat(attackBonus).isEqualTo(swordItem.getAttackBonus() + ringItem.getAttackBonus());
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
        @DisplayName("보유만 하고 장착하지 않으면 반영되지 않는다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item shieldItem = ItemFixture.createShieldItemWithId();
            inventory.addItem(shieldItem);

            assertThat(inventory.getDefenseBonus()).isEqualTo(0);
        }

        @Test
        @DisplayName("서로 다른 타입의 장착 아이템 방어 보너스가 합산된다")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Item shieldItem = ItemFixture.createShieldItemWithId();
            Item ringItem = ItemFixture.createRingItemWithId();
            inventory.addItem(shieldItem);
            inventory.addItem(ringItem);
            inventory.equip(shieldItem.getId());
            inventory.equip(ringItem.getId());

            int defenseBonus = inventory.getDefenseBonus();

            assertThat(defenseBonus).isEqualTo(shieldItem.getDefenseBonus() + ringItem.getDefenseBonus());
        }
    }

    @Nested
    class Equip {

        @Test
        @DisplayName("아이템을 장착하면 equipped 가 true 가 된다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            inventory.addItem(swordItem);

            inventory.equip(swordItem.getId());

            InventoryItem equipped = inventory.getInventoryItems().get(0);
            assertThat(equipped.isEquipped()).isTrue();
        }

        @Test
        @DisplayName("같은 타입의 다른 아이템을 장착하면 기존 장착 아이템은 해제된다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            Item axeItem = ItemFixture.createAxeItemWithId();
            inventory.addItem(swordItem);
            inventory.addItem(axeItem);

            inventory.equip(swordItem.getId());
            inventory.equip(axeItem.getId());

            List<InventoryItem> items = inventory.getInventoryItems();
            assertThat(items)
                    .filteredOn(it -> it.getItem().equals(swordItem))
                    .extracting(InventoryItem::isEquipped)
                    .containsExactly(false);
            assertThat(items)
                    .filteredOn(it -> it.getItem().equals(axeItem))
                    .extracting(InventoryItem::isEquipped)
                    .containsExactly(true);
        }

        @Test
        @DisplayName("보유하지 않은 아이템을 장착하려 하면 EntityNotFoundException")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Long invalidItemId = 999L;

            assertThatThrownBy(() -> inventory.equip(invalidItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class Unequip {

        @Test
        @DisplayName("장착 아이템을 해제하면 equipped 가 false 가 된다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            inventory.addItem(swordItem);
            inventory.equip(swordItem.getId());

            inventory.unequip(swordItem.getId());

            assertThat(inventory.getInventoryItems().get(0).isEquipped()).isFalse();
        }
    }
}