package com.rpg.lab.inventory;

import com.rpg.lab.common.RandomProvider;
import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.exception.InvalidItemOperationException;
import com.rpg.lab.exception.MaxEnhanceLevelException;
import com.rpg.lab.fixture.InventoryFixture;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemSetType;
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
        @DisplayName("장비는 여러 번 addItem 하면 행이 따로 쌓인다")
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

        @Test
        @DisplayName("소모품을 처음 추가하면 수량 1인 행이 생긴다")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Item potion = ItemFixture.createPotionItemWithId();

            inventory.addItem(potion);

            assertThat(inventory.getInventoryItems()).hasSize(1);
            assertThat(inventory.getInventoryItems().getFirst().getQuantity()).isEqualTo(1);
        }

        @Test
        @DisplayName("같은 소모품을 여러 번 추가하면 행은 하나, 수량이 쌓인다")
        void test4() {
            Inventory inventory = Inventory.create(player);
            Item potion = ItemFixture.createPotionItemWithId();

            inventory.addItem(potion);
            inventory.addItem(potion);
            inventory.addItem(potion);

            assertThat(inventory.getInventoryItems()).hasSize(1);
            assertThat(inventory.getInventoryItems().getFirst().getQuantity()).isEqualTo(3);
        }

        @Test
        @DisplayName("addItem 은 생성되거나 수량이 늘어난 행을 반환한다")
        void test5() {
            Inventory inventory = Inventory.create(player);
            Item potion = ItemFixture.createPotionItemWithId();

            InventoryItem first = inventory.addItem(potion);
            InventoryItem second = inventory.addItem(potion);

            assertThat(second).isSameAs(first);
            assertThat(second.getQuantity()).isEqualTo(2);
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

        @Test
        @DisplayName("소모품은 이미 보유해도 추가되고, 같은 행의 수량이 늘어난다")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Item potion = ItemFixture.createPotionItemWithId();
            inventory.addItem(potion);

            inventory.addItemIfNotOwned(potion);

            assertThat(inventory.getInventoryItems()).hasSize(1);
            assertThat(inventory.getInventoryItems().getFirst().getQuantity()).isEqualTo(2);
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
            InventoryItem sword = inventory.addItem(swordItem);
            InventoryItem ring = inventory.addItem(ringItem);
            InventoryFixture.assignIds(inventory);
            inventory.equip(sword.getId());
            inventory.equip(ring.getId());

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
            InventoryItem shield = inventory.addItem(shieldItem);
            InventoryItem ring = inventory.addItem(ringItem);
            InventoryFixture.assignIds(inventory);
            inventory.equip(shield.getId());
            inventory.equip(ring.getId());

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
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);

            inventory.equip(sword.getId());

            assertThat(sword.isEquipped()).isTrue();
        }

        @Test
        @DisplayName("같은 타입의 다른 아이템을 장착하면 기존 장착 아이템은 해제된다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryItem axe = inventory.addItem(ItemFixture.createAxeItemWithId());
            InventoryFixture.assignIds(inventory);

            inventory.equip(sword.getId());
            inventory.equip(axe.getId());

            assertThat(sword.isEquipped()).isFalse();
            assertThat(axe.isEquipped()).isTrue();
        }

        @Test
        @DisplayName("보유하지 않은 아이템을 장착하려 하면 EntityNotFoundException")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Long invalidItemId = 999L;

            assertThatThrownBy(() -> inventory.equip(invalidItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("소모품을 장착하려 하면 InvalidItemOperationException")
        void test4() {
            Inventory inventory = Inventory.create(player);
            InventoryItem potion = inventory.addItem(ItemFixture.createPotionItemWithId());
            InventoryFixture.assignIds(inventory);

            assertThatThrownBy(() -> inventory.equip(potion.getId()))
                    .isInstanceOf(InvalidItemOperationException.class);
        }

        @Test
        @DisplayName("같은 장비가 두 개면 각각 따로 장착할 수 있다")
        void test5() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            InventoryItem sword1 = inventory.addItem(swordItem);
            InventoryItem sword2 = inventory.addItem(swordItem);
            InventoryFixture.assignIds(inventory);

            inventory.equip(sword2.getId());

            assertThat(sword1.isEquipped()).isFalse();
            assertThat(sword2.isEquipped()).isTrue();
        }

        @Test
        @DisplayName("같은 장비 중 하나를 장착한 상태에서 다른 하나를 장착하면 기존 것은 해제된다")
        void test6() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            InventoryItem sword1 = inventory.addItem(swordItem);
            InventoryItem sword2 = inventory.addItem(swordItem);
            InventoryFixture.assignIds(inventory);
            inventory.equip(sword1.getId());

            inventory.equip(sword2.getId());

            assertThat(sword1.isEquipped()).isFalse();
            assertThat(sword2.isEquipped()).isTrue();
        }

        @Test
        @DisplayName("다른 인벤토리의 행 ID로는 장착할 수 없다")
        void test7() {
            Inventory mine = Inventory.create(player);
            Inventory others = Inventory.create(PlayerFixture.create());
            InventoryItem othersSword = others.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(others);

            assertThatThrownBy(() -> mine.equip(othersSword.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class Unequip {

        @Test
        @DisplayName("장착 아이템을 해제하면 equipped 가 false 가 된다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);
            inventory.equip(sword.getId());

            inventory.unequip(sword.getId());

            assertThat(sword.isEquipped()).isFalse();
        }
    }

    @Nested
    class SellItem {

        @Test
        @DisplayName("아이템을 판매하면 판매가를 반환하고 인벤토리에서 사라진다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item item = ItemFixture.createSwordItemWithId();
            InventoryItem sword = inventory.addItem(item);
            InventoryFixture.assignIds(inventory);

            int price = inventory.sellItem(sword.getId());

            assertThat(price).isEqualTo(item.sellPrice());
            assertThat(inventory.getInventoryItems()).doesNotContain(sword);
        }

        @Test
        @DisplayName("장착 중인 아이템도 판매할 수 있다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            InventoryItem axe = inventory.addItem(ItemFixture.createAxeItemWithId());
            InventoryFixture.assignIds(inventory);
            inventory.equip(axe.getId());

            inventory.sellItem(axe.getId());

            assertThat(inventory.getInventoryItems()).isEmpty();
        }

        @Test
        @DisplayName("보유하지 않은 아이템을 판매하려 하면 EntityNotFoundException")
        void test3() {
            Inventory inventory = Inventory.create(player);
            Long notOwnedItemId = 999L;

            assertThatThrownBy(() -> inventory.sellItem(notOwnedItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("소모품을 판매하면 수량만 1 줄어든다")
        void test4() {
            Inventory inventory = Inventory.create(player);
            Item potionItem = ItemFixture.createPotionItemWithId();
            inventory.addItem(potionItem);
            InventoryItem potion = inventory.addItem(potionItem);
            InventoryFixture.assignIds(inventory);

            int price = inventory.sellItem(potion.getId());

            assertThat(price).isEqualTo(potionItem.sellPrice());
            assertThat(inventory.getInventoryItems()).hasSize(1);
            assertThat(potion.getQuantity()).isEqualTo(1);
        }

        @Test
        @DisplayName("마지막 소모품을 판매하면 행이 사라진다")
        void test5() {
            Inventory inventory = Inventory.create(player);
            InventoryItem potion = inventory.addItem(ItemFixture.createPotionItemWithId());
            InventoryFixture.assignIds(inventory);

            inventory.sellItem(potion.getId());

            assertThat(inventory.getInventoryItems()).isEmpty();
        }

        @Test
        @DisplayName("같은 장비가 두 개면 지정한 것만 판매된다")
        void test6() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            InventoryItem sword1 = inventory.addItem(swordItem);
            InventoryItem sword2 = inventory.addItem(swordItem);
            InventoryFixture.assignIds(inventory);
            inventory.equip(sword1.getId());

            inventory.sellItem(sword2.getId());

            assertThat(inventory.getInventoryItems()).containsExactly(sword1);
            assertThat(sword1.isEquipped()).isTrue();
        }
    }

    @Nested
    class Enhance {

        private final EnhancementPolicy alwaysSuccessPolicy = new EnhancementPolicy(
                List.of(
                        new EnhanceRate(0, 100),
                        new EnhanceRate(1, 100),
                        new EnhanceRate(2, 100),
                        new EnhanceRate(3, 100),
                        new EnhanceRate(4, 100),
                        new EnhanceRate(5, 100),
                        new EnhanceRate(6, 100),
                        new EnhanceRate(7, 100),
                        new EnhanceRate(8, 100),
                        new EnhanceRate(9, 100)
                ),
                50
        );

        private final EnhancementPolicy alwaysFailPolicy = new EnhancementPolicy(
                List.of(
                        new EnhanceRate(0, 0),
                        new EnhanceRate(1, 0),
                        new EnhanceRate(2, 0),
                        new EnhanceRate(3, 0),
                        new EnhanceRate(4, 0),
                        new EnhanceRate(5, 0),
                        new EnhanceRate(6, 0),
                        new EnhanceRate(7, 0),
                        new EnhanceRate(8, 0),
                        new EnhanceRate(9, 0)
                ),
                50
        );

        private final RandomProvider fixedRoll = new RandomProvider() {
            @Override
            public double nextDouble() {
                return 0;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };

        @Test
        @DisplayName("강화에 성공하면 enhanceLevel 이 오른다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);

            EnhanceResult result = inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll);

            assertThat(result.success()).isTrue();
            assertThat(result.newEnhanceLevel()).isEqualTo(1);
        }

        @Test
        @DisplayName("강화에 실패하면 enhanceLevel 이 내려간다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);
            inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll);

            EnhanceResult result = inventory.enhanceItem(sword.getId(), alwaysFailPolicy, fixedRoll);

            assertThat(result.success()).isFalse();
            assertThat(result.newEnhanceLevel()).isEqualTo(0);
        }

        @Test
        @DisplayName("최소 레벨(0) 에서 실패해도 레벨이 음수가 되지 않는다")
        void test3() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);

            EnhanceResult result = inventory.enhanceItem(sword.getId(), alwaysFailPolicy, fixedRoll);

            assertThat(result.success()).isFalse();
            assertThat(result.newEnhanceLevel()).isEqualTo(0);
        }

        @Test
        @DisplayName("이미 최대 레벨이면 MaxEnhanceLevelException 이 발생한다")
        void test4() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);

            for (int i = 0; i < EnhancementPolicy.MAX_ENHANCE_LEVEL; i++) {
                inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll);
            }

            assertThatThrownBy(() -> inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll))
                    .isInstanceOf(MaxEnhanceLevelException.class);
        }

        @Test
        @DisplayName("강화한 장착 아이템은 강화 단계만큼 공격 보너스가 더 붙는다")
        void test5() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId()); // atk 5
            InventoryFixture.assignIds(inventory);
            inventory.equip(sword.getId());
            inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll); // +1

            assertThat(inventory.getAttackBonus()).isEqualTo(6); // 5 + ceil(5*1*20/100) = 5+1
        }

        @Test
        @DisplayName("무기를 강화해도 방어 보너스는 오르지 않는다")
        void test6() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId()); // def 0
            InventoryFixture.assignIds(inventory);
            inventory.equip(sword.getId());
            inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll);

            assertThat(inventory.getDefenseBonus()).isEqualTo(0);
        }

        @Test
        @DisplayName("장착하지 않은 아이템은 강화해도 보너스에 반영되지 않는다")
        void test7() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);
            inventory.enhanceItem(sword.getId(), alwaysSuccessPolicy, fixedRoll);

            assertThat(inventory.getAttackBonus()).isEqualTo(0);
        }

        @Test
        @DisplayName("세트 아이템을 강화하면 세트 보너스도 함께 커진다")
        void test8() {
            Inventory inventory = Inventory.create(player);
            InventoryItem weapon = inventory.addItem(ItemFixture.createGoblinSetWeaponWithId()); // ATK 3
            InventoryItem armor = inventory.addItem(ItemFixture.createGoblinSetArmorWithId());
            InventoryFixture.assignIds(inventory);
            inventory.equip(weapon.getId());
            inventory.equip(armor.getId());
            inventory.enhanceItem(weapon.getId(), alwaysSuccessPolicy, fixedRoll); // 단검 +1 → ATK 4

            // 단검 3 → +1 강화(20% 올림) → 4
            // 세트 보너스: 세트 아이템 ATK 합(4 + 0) × 100% = 4
            assertThat(inventory.getAttackBonus()).isEqualTo(4 + 4);
        }

        @Test
        @DisplayName("소모품을 강화하려 하면 InvalidItemOperationException")
        void test9() {
            Inventory inventory = Inventory.create(player);
            InventoryItem potion = inventory.addItem(ItemFixture.createPotionItemWithId());
            InventoryFixture.assignIds(inventory);

            assertThatThrownBy(() -> inventory.enhanceItem(potion.getId(), alwaysSuccessPolicy, fixedRoll))
                    .isInstanceOf(InvalidItemOperationException.class);
        }

        @Test
        @DisplayName("같은 장비가 두 개면 지정한 것만 강화된다")
        void test10() {
            Inventory inventory = Inventory.create(player);
            Item swordItem = ItemFixture.createSwordItemWithId();
            InventoryItem sword1 = inventory.addItem(swordItem);
            InventoryItem sword2 = inventory.addItem(swordItem);
            InventoryFixture.assignIds(inventory);

            inventory.enhanceItem(sword2.getId(), alwaysSuccessPolicy, fixedRoll);

            assertThat(sword1.getEnhanceLevel()).isEqualTo(0);
            assertThat(sword2.getEnhanceLevel()).isEqualTo(1);
        }
    }

    @Nested
    class SetBonus {
        @Test
        @DisplayName("세트 아이템을 2개 미만 장착하면 세트 보너스가 없다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item goblinWeaponItem = ItemFixture.createGoblinSetWeaponWithId();
            InventoryItem goblinWeapon = inventory.addItem(goblinWeaponItem);
            InventoryFixture.assignIds(inventory);
            inventory.equip(goblinWeapon.getId());

            int attackBonus = inventory.getAttackBonus();

            assertThat(attackBonus).isEqualTo(goblinWeaponItem.getAttackBonus());
        }

        @Test
        @DisplayName("세트 아이템을 2개 이상 장착하면 세트 보너스가 추가된다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item goblinWeaponItem = ItemFixture.createGoblinSetWeaponWithId();
            Item goblinArmorItem = ItemFixture.createGoblinSetArmorWithId();
            InventoryItem goblinWeapon = inventory.addItem(goblinWeaponItem);
            InventoryItem goblinArmor = inventory.addItem(goblinArmorItem);
            InventoryFixture.assignIds(inventory);
            inventory.equip(goblinWeapon.getId());
            inventory.equip(goblinArmor.getId());

            int attackBonus = inventory.getAttackBonus();
            int defenseBonus = inventory.getDefenseBonus();

            assertThat(attackBonus).isEqualTo(goblinWeaponItem.getAttackBonus() + goblinArmorItem.getAttackBonus() + 3);
            assertThat(defenseBonus).isEqualTo(goblinWeaponItem.getDefenseBonus() + goblinArmorItem.getDefenseBonus() + 3);
        }

        @Test
        @DisplayName("활성화된 세트 목록을 조회할 수 있다")
        void test3() {
            Inventory inventory = Inventory.create(player);
            InventoryItem goblinWeapon = inventory.addItem(ItemFixture.createGoblinSetWeaponWithId());
            InventoryItem goblinArmor = inventory.addItem(ItemFixture.createGoblinSetArmorWithId());
            InventoryFixture.assignIds(inventory);
            inventory.equip(goblinWeapon.getId());
            inventory.equip(goblinArmor.getId());

            assertThat(inventory.getActiveSets()).containsExactly(ItemSetType.GOBLIN_SET);
        }
    }

    @Nested
    class UseItem {

        @Test
        @DisplayName("소모품을 사용하면 회복량을 반환하고 인벤토리에서 사라진다")
        void test1() {
            Inventory inventory = Inventory.create(player);
            Item potionItem = ItemFixture.createPotionItemWithId();
            InventoryItem potion = inventory.addItem(potionItem);
            InventoryFixture.assignIds(inventory);

            int healAmount = inventory.useItem(potion.getId());

            assertThat(healAmount).isEqualTo(potionItem.getHealAmount());
            assertThat(inventory.getInventoryItems()).isEmpty();
        }

        @Test
        @DisplayName("같은 포션이 여러 개면 수량만 1 줄어든다")
        void test2() {
            Inventory inventory = Inventory.create(player);
            Item potionItem = ItemFixture.createPotionItemWithId();
            inventory.addItem(potionItem);
            inventory.addItem(potionItem);
            InventoryItem potion = inventory.addItem(potionItem);
            InventoryFixture.assignIds(inventory);

            inventory.useItem(potion.getId());

            assertThat(inventory.getInventoryItems()).hasSize(1);
            assertThat(potion.getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("장비를 사용하려 하면 InvalidItemOperationException")
        void test3() {
            Inventory inventory = Inventory.create(player);
            InventoryItem sword = inventory.addItem(ItemFixture.createSwordItemWithId());
            InventoryFixture.assignIds(inventory);

            assertThatThrownBy(() -> inventory.useItem(sword.getId()))
                    .isInstanceOf(InvalidItemOperationException.class);
        }

        @Test
        @DisplayName("보유하지 않은 아이템을 사용하려 하면 EntityNotFoundException")
        void test4() {
            Inventory inventory = Inventory.create(player);

            assertThatThrownBy(() -> inventory.useItem(999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}