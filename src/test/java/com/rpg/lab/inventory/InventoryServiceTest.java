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

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventoryRepository.save(Inventory.create(player));
    }

    @Nested
    class EquipItem {

        @Test
        @DisplayName("아이템을 장착하면 인벤토리에 반영된다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());

            InventoryResponse result = sut.equipItem(player.getId(), item.getId());

            assertThat(result.items())
                    .extracting(ItemResponse::name)
                    .contains(item.getName());
        }

        @Test
        @DisplayName("존재하지 않는 player 로 장착 시도하면 EntityNotFoundException")
        void test2() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            Long invalidPlayerId = 999L;

            assertThatThrownBy(() -> sut.equipItem(invalidPlayerId, item.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 item 으로 장착 시도하면 EntityNotFoundException")
        void test3() {
            Long invalidItemId = 999L;

            assertThatThrownBy(() -> sut.equipItem(player.getId(), invalidItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class UnequipItem {

        @Test
        @DisplayName("아이템을 해제하면 인벤토리에서 사라진다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            sut.equipItem(player.getId(), item.getId());

            InventoryResponse result = sut.unequipItem(player.getId(), item.getId());

            assertThat(result.items())
                    .extracting(ItemResponse::name)
                    .doesNotContain(item.getName());
        }

        @Test
        @DisplayName("보유하지 않은 item을 해제해도 예외 없이 그냥 무시된다")
        void test2() {
            Long notOwnedItemId = 999L;

            InventoryResponse result = sut.unequipItem(player.getId(), notOwnedItemId);

            assertThat(result.items()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 player 로 해제 시도하면 EntityNotFoundException")
        void test3() {
            Long invalidPlayerId = 999L;

            assertThatThrownBy(() -> sut.unequipItem(invalidPlayerId, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}