package com.rpg.lab.inventory;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.exception.ErrorCode;
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
            InventoryItem sword = ownItem();

            List<Boolean> equippedFlags = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                equipItem(inventoryItemId: "%d") { items { inventoryItemId equipped } }
                            }
                            """.formatted(sword.getId()),
                    "data.equipItem.items[?(@.inventoryItemId=='%d')].equipped".formatted(sword.getId()),
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(equippedFlags).containsExactly(true);
        }

        @Test
        @DisplayName("소모품을 장착하려 하면 INVALID_ITEM_OPERATION 에러 코드가 내려온다")
        void test2() {
            InventoryItem potion = own(itemRepository.save(ItemFixture.createPotionItem()));

            ExecutionResult result = dgsQueryExecutor.execute(
                    """
                    mutation { equipItem(inventoryItemId: "%d") { id } }
                    """.formatted(potion.getId()),
                    Collections.emptyMap(), null, headers);

            assertThat(result.getErrors().getFirst().getExtensions())
                    .containsEntry("errorCode", ErrorCode.INVALID_ITEM_OPERATION.name())
                    .containsEntry("userMessage", ErrorCode.INVALID_ITEM_OPERATION.getUserMessage());
        }

        @Test
        @DisplayName("같은 장비가 두 개면 지정한 행만 장착된 것으로 응답된다")
        void test3() {
            Item swordItem = itemRepository.save(ItemFixture.createSwordItem());
            inventory.addItem(swordItem);
            inventory.addItem(swordItem);
            inventoryRepository.save(inventory);
            List<InventoryItem> swords = inventoryRepository.findWithItemsByPlayerId(player.getId())
                    .orElseThrow()
                    .getInventoryItems();
            InventoryItem sword1 =swords.get(0);
            InventoryItem sword2 =swords.get(1);

            String query = """
                    mutation {
                        equipItem(inventoryItemId: "%d") { items { inventoryItemId equipped } }
                    }
            """.formatted(sword2.getId());

            List<Boolean> sword1Equipped = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    query,
                    "data.equipItem.items[?(@.inventoryItemId=='%d')].equipped".formatted(sword1.getId()),
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(sword1Equipped).containsExactly(false);
        }
    }

    @Nested
    class UnequipItem {

        @Test
        @DisplayName("장착한 아이템을 해제하면 equipped가 false로 응답된다")
        void test1() {
            InventoryItem sword = ownItem();
            inventory.equip(sword.getId());
            inventoryRepository.save(inventory);

            List<Boolean> equippedFlags = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                unequipItem(inventoryItemId: "%d") { items { inventoryItemId equipped } }
                            }
                            """.formatted(sword.getId()),
                    "data.unequipItem.items[?(@.inventoryItemId=='%d')].equipped".formatted(sword.getId()),
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
            InventoryItem sword = ownItem();

            List<Long> inventoryItemIds = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                sellItem(inventoryItemId: "%d") { items { inventoryItemId } }
                            }
                            """.formatted(sword.getId()),
                    "data.sellItem.items[*].inventoryItemId",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(inventoryItemIds).doesNotContain(sword.getId());
        }
    }

    @Nested
    class EnhanceItem {

        @Test
        @DisplayName("강화하면 성공 여부와 새 강화 단계가 응답된다")
        void test1() {
            InventoryItem sword = ownItem();
            player.gainGold(1000);
            playerRepository.save(player);

            Boolean result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                enhanceItem(inventoryItemId: "%d") { success newEnhanceLevel }
                            }
                            """.formatted(sword.getId()),
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
            InventoryItem sword = ownItem();

            ExecutionResult result = dgsQueryExecutor.execute(
                    """
                            mutation {
                                enhanceItem(inventoryItemId: "%d") { success }
                            }
                            """.formatted(sword.getId()),
                    Collections.emptyMap(),
                    null,
                    headers
            );

            assertThat(result.getErrors()).isNotEmpty();
        }

        @Test
        @DisplayName("최대 강화 단계에서 강화하면 MAX_ENHANCE_LEVEL 에러 코드가 내려온다")
        void test3() {
            InventoryItem sword = ownItem();
            player.gainGold(100_000);
            playerRepository.save(player);
            String mutation = """
                                mutation { enhanceItem(inventoryItemId: "%d") { success } }
                                """.formatted(sword.getId());

            for (int i = 0; i < EnhancementPolicy.MAX_ENHANCE_LEVEL; i++) {
                dgsQueryExecutor.execute(mutation, Collections.emptyMap(), null, headers);
            }

            ExecutionResult result = dgsQueryExecutor.execute(mutation, Collections.emptyMap(), null, headers);

            assertThat(result.getErrors().getFirst().getExtensions())
                    .containsEntry("errorCode", ErrorCode.MAX_ENHANCE_LEVEL.name())
                    .containsEntry("userMessage", ErrorCode.MAX_ENHANCE_LEVEL.getUserMessage());
        }
    }

    @Nested
    class ActiveSets {

        @Test
        @DisplayName("세트 아이템을 2개 이상 장착하면 장착 응답에 활성 세트가 담긴다")
        void test1() {
            InventoryItem weapon = own(itemRepository.save(ItemFixture.createGoblinSetWeapon()));
            InventoryItem armor = own(itemRepository.save(ItemFixture.createGoblinSetArmor()));
            inventory.equip(weapon.getId());
            inventoryRepository.save(inventory);

            List<String> activeSets = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                    mutation {
                        equipItem(inventoryItemId: "%d") { activeSets }
                    }
                    """.formatted(armor.getId()),
                    "data.equipItem.activeSets",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(activeSets).containsExactly("GOBLIN_SET");
        }

        @Test
        @DisplayName("세트 아이템이 아니면 activeSets가 비어 있다")
        void test2() {
            InventoryItem sword = ownItem();

            List<String> activeSets = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                    mutation {
                        equipItem(inventoryItemId: "%d") { activeSets }
                    }
                    """.formatted(sword.getId()),
                    "data.equipItem.activeSets",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(activeSets).isEmpty();
        }
    }

    @Nested
    class UseItem {

        @Test
        @DisplayName("포션을 사용하면 회복량이 응답된다")
        void test1() {
            InventoryItem potion = own(itemRepository.save(ItemFixture.createPotionItem()));
            player.syncHp(50);
            playerRepository.save(player);

            Integer healed = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                useItem(inventoryItemId: "%d") { healedAmount }
                            }
                            """.formatted(potion.getId()),
                    "data.useItem.healedAmount",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(healed).isEqualTo(30);
        }

        @Test
        @DisplayName("풀피에 포션을 사용하면 HP_ALREADY_FULL 에러 코드가 내려온다")
        void test2() {
            InventoryItem potion = own(itemRepository.save(ItemFixture.createPotionItem()));

            ExecutionResult result = dgsQueryExecutor.execute(
                    """
                            mutation { useItem(inventoryItemId: "%d") { healedAmount } }
                            """.formatted(potion.getId()),
                    Collections.emptyMap(),
                    null,
                    headers
            );

            assertThat(result.getErrors()).isNotEmpty();
            assertThat(result.getErrors().getFirst().getExtensions())
                    .containsEntry("errorCode", ErrorCode.HP_ALREADY_FULL.name())
                    .containsEntry("userMessage", ErrorCode.HP_ALREADY_FULL.getUserMessage());
        }

        @Test
        @DisplayName("포션을 사용하면 응답의 inventory 에 줄어든 수량이 담긴다")
        void test3() {
            Item potionItem = itemRepository.save(ItemFixture.createPotionItem());
            inventory.addItem(potionItem);
            InventoryItem potion = own(potionItem);   // 두 번째 addItem → 수량 2
            player.syncHp(50);
            playerRepository.save(player);

            List<Integer> quantities = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                useItem(inventoryItemId: "%d") { inventory { items { quantity } } }
                            }
                            """.formatted(potion.getId()),
                    "data.useItem.inventory.items[*].quantity",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(quantities).containsExactly(1);
        }
    }

    /**
     * 검을 하나 지급하고, 저장된 인벤토리 행(ID 포함)을 반환한다.
     */
    private InventoryItem ownItem() {
        return own(itemRepository.save(ItemFixture.createSwordItem()));
    }

    /**
     * 아이템을 지급하고, 저장된 인벤토리 행(ID 포함)을 반환한다.
     * 조회 쿼리 직전 auto-flush 로 insert 가 나가므로 ID 가 채워진 행을 받을 수 있다.
     */
    private InventoryItem own(Item item) {
        inventory.addItem(item);
        inventoryRepository.save(inventory);
        return inventoryRepository.findWithItemsByPlayerId(player.getId())
                .orElseThrow()
                .getInventoryItems().stream()
                .filter(it -> it.getItem().getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();
    }
}