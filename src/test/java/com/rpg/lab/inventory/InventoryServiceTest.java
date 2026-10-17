package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.exception.InsufficientGoldException;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemRepository;
import com.rpg.lab.item.ItemResponse;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class InventoryServiceTest {

    private final InventoryService sut;

    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    private Player player;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventory = inventoryRepository.save(Inventory.create(player));
    }

    @Nested
    class EquipItem {

        @Test
        @DisplayName("보유한 아이템을 장착하면 equipped 상태가 된다")
        void test1() {
            Item item = ownItem();

            InventoryResponse result = sut.equipItem(player.getId(), item.getId());

            assertThat(result.items())
                    .filteredOn(i -> i.id().equals(item.getId()))
                    .extracting(
                            ItemResponse::name,
                            ItemResponse::equipped
                    )
                    .contains(Tuple.tuple(item.getName(), true));
        }

        @Test
        @DisplayName("존재하지 않는 player 로 장착 시도하면 EntityNotFoundException")
        void test2() {
            Item item = ownItem();
            Long invalidPlayerId = 999L;

            assertThatThrownBy(() -> sut.equipItem(invalidPlayerId, item.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("보유하지 않은 item 으로 장착 시도하면 EntityNotFoundException")
        void test3() {
            Long invalidItemId = 999L;

            assertThatThrownBy(() -> sut.equipItem(player.getId(), invalidItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class UnequipItem {

        @Test
        @DisplayName("장착한 아이템을 해제하면 equipped 가 false 가 된다")
        void test1() {
            Item item = ownItem();
            sut.equipItem(player.getId(), item.getId());

            InventoryResponse result = sut.unequipItem(player.getId(), item.getId());

            assertThat(result.items())
                    .filteredOn(i -> i.id().equals(item.getId()))
                    .extracting(
                            ItemResponse::name,
                            ItemResponse::equipped
                    )
                    .containsExactly(Tuple.tuple(item.getName(), false));
        }

        @Test
        @DisplayName("보유하지 않은 item을 해제하려 하면 EntityNotFoundException")
        void test2() {
            Long notOwnedItemId = 999L;

            assertThatThrownBy(() -> sut.unequipItem(player.getId(), notOwnedItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 player 로 해제 시도하면 EntityNotFoundException")
        void test3() {
            Item item = ownItem();
            Long invalidPlayerId = 999L;

            assertThatThrownBy(() -> sut.unequipItem(invalidPlayerId, item.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class SellItem {

        @Test
        @DisplayName("아이템을 판매하면 인벤토리에서 사라지고 골드를 얻는다")
        void test1() {
            Item item = ownItem();
            int goldBefore = player.getGold();

            InventoryResponse result = sut.sellItem(player.getId(), item.getId());

            assertThat(result.items()).extracting(ItemResponse::id).doesNotContain(item.getId());
            assertThat(player.getGold()).isEqualTo(goldBefore + item.sellPrice());
        }

        @Test
        @DisplayName("보유하지 않은 item 을 판매하려 하면 EntityNotFoundException")
        void test2() {
            Long notOwnedItemId = 999L;

            assertThatThrownBy(() -> sut.sellItem(player.getId(), notOwnedItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("장착 중인 아이템도 판매할 수 있다")
        void test3() {
            Item item = ownItem();
            sut.equipItem(player.getId(), item.getId());

            InventoryResponse result = sut.sellItem(player.getId(), item.getId());

            assertThat(result.items()).isEmpty();
        }
    }

    @Nested
    class EnhanceItem {

        @Test
        @DisplayName("강화에 성공하면 enhanceLevel 이 오르고 골드가 차감된다")
        void test1() {
            Item item = ownItem();
            player.gainGold(1000);
            playerRepository.save(player);
            int goldBefore = player.getGold();

            EnhanceItemResponse result = sut.enhanceItem(player.getId(), item.getId());

            assertThat(result.success()).isTrue();
            assertThat(result.newEnhanceLevel()).isEqualTo(1);
            assertThat(player.getGold()).isEqualTo(goldBefore - 50);
        }

        @Test
        @DisplayName("골드가 부족하면 InsufficientGoldException 이 발생한다")
        void test2() {
            Item item = ownItem();

            assertThatThrownBy(() -> sut.enhanceItem(player.getId(), item.getId()))
                    .isInstanceOf(InsufficientGoldException.class);
        }

        @Test
        @DisplayName("보유하지 않은 item 을 강화하려 하면 EntityNotFoundException")
        void test3() {
            player.gainGold(1000);
            playerRepository.save(player);
            Long nowOwnedItemId = 999L;

            assertThatThrownBy(() -> sut.enhanceItem(player.getId(), nowOwnedItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("최대 레벨에서 강화 시도하면 IllegalStateException 이 발생한다")
        void test4() {
            Item item = ownItem();
            player.gainGold(100_000);
            playerRepository.save(player);
            for (int i = 0; i < EnhancementPolicy.MAX_ENHANCE_LEVEL; i++) {
                sut.enhanceItem(player.getId(), item.getId());
            }

            assertThatThrownBy(() -> sut.enhanceItem(player.getId(), item.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class UseItem {

        @Test
        @DisplayName("포션을 사용하면 hp 가 회복되고 포션이 하나 사라진다")
        void test1() {
            Item potion = ownPotion();
            player.syncHp(50);
            playerRepository.save(player);

            UseItemResponse result = sut.useItem(player.getId(), potion.getId());

            assertThat(result.healedAmount()).isEqualTo(30);
            assertThat(player.getHp()).isEqualTo(80);
            assertThat(result.inventor().items()).isEmpty();
        }

        @Test
        @DisplayName("최대 hp 를 넘는 회복량은 잘리고 실제 회복량만 반환한다")
        void test2() {
            Item potion = ownPotion();
            player.syncHp(player.getMaxHp() - 10);
            playerRepository.save(player);

            UseItemResponse result = sut.useItem(player.getId(), potion.getId());

            assertThat(result.healedAmount()).isEqualTo(10);
            assertThat(player.getHp()).isEqualTo(player.getMaxHp());
        }

        @Test
        @DisplayName("이미 체력이 가득 차 있으면 사용할 수 없고 포션은 그대로 남는다")
        void test3() {
            Item potion = ownPotion();

            assertThatThrownBy(() -> sut.useItem(player.getId(), potion.getId()))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(inventory.getInventoryItems()).hasSize(1);
        }

        @Test
        @DisplayName("장비는 사용할 수 없다")
        void test4() {
            Item sword = ownItem();
            player.syncHp(50);
            playerRepository.save(player);

            assertThatThrownBy(() -> sut.useItem(player.getId(), sword.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("보유하지 않은 아이템은 사용할 수 없다")
        void test5() {
            player.syncHp(50);
            playerRepository.save(player);

            assertThatThrownBy(() -> sut.useItem(player.getId(), 999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    private Item ownItem() {
        Item item = itemRepository.save(ItemFixture.createSwordItem());
        inventory.addItem(item);
        inventoryRepository.save(inventory);
        return item;
    }

    private Item ownPotion() {
        Item potion = itemRepository.save(ItemFixture.createPotionItem());
        inventory.addItem(potion);
        inventoryRepository.save(inventory);
        return potion;
    }
}