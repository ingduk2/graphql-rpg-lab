package com.rpg.lab.inventory;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
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
class InventoryDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final ItemRepository itemRepository;
    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    private Player player;
    private Inventory inventory;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventory = inventoryRepository.save(Inventory.create(player));
        headers = new HttpHeaders();
        headers.add(PlayerContextBuilder.X_PLAYER_ID, String.valueOf(player.getId()));
    }

    @Nested
    class EquipItem {

        @Test
        @DisplayName("보유한 아이템을 장착하면 equipped 가 true 로 응답된다")
        void test1() {
            Item item = ownItem();

            List<Boolean> equippedFlags = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                equipItem(itemId: "%d") { items { id equipped } }
                            }
                            """.formatted(item.getId()),
                    "data.equipItem.items[?(@.id=='%d')].equipped".formatted(item.getId()),
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(equippedFlags).containsExactly(true);
        }
    }

    @Nested
    class UnequipItem {

        @Test
        @DisplayName("장착한 아이템을 해제하면 equipped가 false로 응답된다")
        void test1() {
            Item item = ownItem();
            inventory.equip(item.getId());
            inventoryRepository.save(inventory);

            List<Boolean> equippedFlags = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                unequipItem(itemId: "%d") { items { id equipped } }
                            }
                            """.formatted(item.getId()),
                    "data.unequipItem.items[?(@.id=='%d')].equipped".formatted(item.getId()),
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(equippedFlags).containsExactly(false);
        }
    }

    @Nested
    class SellItem {

        @Test
        @DisplayName("아이템을 판매하면 인벤토리에서 사라진다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            inventory.addItem(item);
            inventoryRepository.save(inventory);

            List<Long> itemIds = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                sellItem(itemId: "%d") { items { id } }
                            }
                            """.formatted(item.getId()),
                    "data.sellItem.items[*].id",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(itemIds).doesNotContain(item.getId());
        }
    }

    @Nested
    class EnhanceItem {

        @Test
        @DisplayName("강화하면 성공 여부와 새 강화 단계가 응답된다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            inventory.addItem(item);
            inventoryRepository.save(inventory);
            player.gainGold(1000);
            playerRepository.save(player);

            Boolean result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                enhanceItem(itemId: "%d") { success newEnhanceLevel }
                            }
                            """.formatted(item.getId()),
                    "data.enhanceItem.success",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("골드가 부족하면 GraphQL 에러가 발생한다")
        void test2() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            inventory.addItem(item);
            inventoryRepository.save(inventory);

            ExecutionResult result = dgsQueryExecutor.execute(
                    """
                            mutation {
                                enhanceItem(itemId: "%d") { success }
                            }
                            """.formatted(item.getId()),
                    Collections.emptyMap(),
                    null,
                    headers
            );

            assertThat(result.getErrors()).isNotEmpty();
        }
    }

    private Item ownItem() {
        Item item = itemRepository.save(ItemFixture.createSwordItem());
        inventory.addItem(item);
        inventoryRepository.save(inventory);
        return item;
    }
}