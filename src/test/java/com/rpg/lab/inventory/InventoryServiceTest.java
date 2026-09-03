package com.rpg.lab.inventory;

import com.rpg.lab.exception.EntityNotFoundException;
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

    private Item ownItem() {
        Item item = itemRepository.save(ItemFixture.createSwordItem());
        inventory.addItem(item);
        inventoryRepository.save(inventory);
        return item;
    }
}