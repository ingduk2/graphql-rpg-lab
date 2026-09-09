package com.rpg.lab.shop;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class ShopDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final ShopItemRepository shopItemRepository;
    private final ItemRepository itemRepository;
    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    private Player player;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventoryRepository.save(Inventory.create(player));
        headers = new HttpHeaders();
        headers.add(PlayerContextBuilder.X_PLAYER_ID, String.valueOf(player.getId()));
    }

    @Nested
    class ShopItems {

        @Test
        @DisplayName("상점 아이템 목록을 조회한다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            shopItemRepository.save(ShopItem.create(item, 50));

            List<String> names = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { shopItems { itemName price } }
                            """,
                    "data.shopItems[*].itemName",
                    new TypeRef<>() {}
            );

            assertThat(names).contains(item.getName());
        }
    }

    @Nested
    class BuyItem {

        @Test
        @DisplayName("골드가 충분하면 구매 성공하고 인벤토리에 아이템이 추가된다")
        void test1() {
            player.gainGold(100);
            playerRepository.save(player);
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            ShopItem shopItem = shopItemRepository.save(ShopItem.create(item, 50));

            List<String> itemIds = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                buyItem(shopItemId: "%d") { items { id } }
                            }
                            """.formatted(shopItem.getId()),
                    "data.buyItem.items[*].id",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(itemIds).contains(String.valueOf(item.getId()));
        }

        @Test
        @DisplayName("골드가 부족하면 GraphQL 에러가 발생한다")
        void test2() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            ShopItem shopItem = shopItemRepository.save(ShopItem.create(item, 999));

            ExecutionResult result = dgsQueryExecutor.execute(
                    """
                            mutation {
                                buyItem(shopItemId: "%d") { id }
                            }
                            """.formatted(shopItem.getId()),
                    Collections.emptyMap(),
                    null,
                    headers
            );

            assertThat(result.getErrors()).isNotEmpty();
        }
    }

}