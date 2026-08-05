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
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventoryRepository.save(Inventory.create(player));
        headers = new HttpHeaders();
        headers.add(PlayerContextBuilder.X_PLAYER_ID, String.valueOf(player.getId()));
    }

    @Nested
    class EquipItem {

        @Test
        @DisplayName("아이템을 장착하면 인벤토리에 반영된다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());

            List<String> itemNames = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                equipItem(itemId: "%d") { items { name } }
                            }
                            """.formatted(item.getId()),
                    "data.equipItem.items[*].name",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(itemNames).contains(item.getName());
        }
    }

    @Nested
    class UnequipItem {

        @Test
        @DisplayName("아이템을 해제하면 인벤토리에서 사라진다")
        void test1() {
            Item item = itemRepository.save(ItemFixture.createSwordItem());
            Inventory inventory = inventoryRepository.findByPlayerId(player.getId()).orElseThrow();
            inventory.addItem(item);
            inventoryRepository.save(inventory);

            List<String> itemNames = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                unequipItem(itemId: "%d") { items { name } }
                            }
                            """.formatted(item.getId()),
                    "data.unequipItem.items[*].name",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(itemNames).doesNotContain(item.getName());
        }
    }
}