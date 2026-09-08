package com.rpg.lab.shop;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.exception.InsufficientGoldException;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.inventory.InventoryResponse;
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
class ShopServiceTest {

    private final ShopService sut;
    private final ShopItemRepository shopItemRepository;
    private final ItemRepository itemRepository;
    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    private Player player;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventoryRepository.save(Inventory.create(player));
    }

    @Nested
    class BuyItem {

        @Test
        @DisplayName("골드가 충분하면 구매 성공, 골드 차감되고 인벤토리에 추가된다")
        void test1() {
            player.gainGold(100);
            playerRepository.save(player);
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            ShopItem shopItem = shopItemRepository.save(ShopItem.create(item, 50));

            InventoryResponse result = sut.buyItem(player.getId(), shopItem.getId());

            assertThat(result.items())
                    .extracting(ItemResponse::id)
                    .contains(item.getId());
            assertThat(player.getGold()).isEqualTo(50);
        }

        @Test
        @DisplayName("골드가 부족하면 InsufficientGoldException 이 발생한다")
        void test2() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            ShopItem shopItem = shopItemRepository.save(ShopItem.create(item, 999));

            assertThatThrownBy(() -> sut.buyItem(player.getId(), shopItem.getId()))
                    .isInstanceOf(InsufficientGoldException.class);
        }

        @Test
        @DisplayName("존재하지 않는 shopItemId 로 구매하면 EntityNotFoundException")
        void test3() {
            Long invalidShopItemId = 999L;

            assertThatThrownBy(() -> sut.buyItem(player.getId(), invalidShopItemId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}