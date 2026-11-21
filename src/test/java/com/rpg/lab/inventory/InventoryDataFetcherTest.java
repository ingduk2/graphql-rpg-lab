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

    // 23-2 시점: mutation 인자 이름은 아직 itemId 지만, 값은 인벤토리 행 ID 를 넘긴다.
    // 응답의 items[].id 는 아직 아이템 ID 라서 JsonPath 필터는 아이템 ID 로 건다. (23-3 에서 정리)

    @Nested
    class EquipItem {

        @Test
        @DisplayName("보유한 아이템을 장착하면 equipped 가 true 로 응답된다")
        void test1() {
            InventoryItem sword = ownItem();

            List<Boolean> equippedFlags = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                equipItem(itemId: "%d") { items { id equipped } }
                            }
                            """.formatted(sword.getId()),
                    "data.equipItem.items[?(@.id=='%d')].equipped".formatted(sword.getItem().getId()),
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
                    mutation { equipItem(itemId: "%d") { id } }
                    """.formatted(potion.getId()),
                    Collections.emptyMap(), null, headers);

            assertThat(result.getErrors().getFirst().getExtensions())
                    .containsEntry("errorCode", ErrorCode.INVALID_ITEM_OPERATION.name())
                    .containsEntry("userMessage", ErrorCode.INVALID_ITEM_OPERATION.getUserMessage());
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
                                unequipItem(itemId: "%d") { items { id equipped } }
                            }
                            """.formatted(sword.getId()),
                    "data.unequipItem.items[?(@.id=='%d')].equipped".formatted(sword.getItem().getId()),
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

            List<Long> itemIds = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                sellItem(itemId: "%d") { items { id } }
                            }
                            """.formatted(sword.getId()),
                    "data.sellItem.items[*].id",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(itemIds).doesNotContain(sword.getItem().getId());
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
                                enhanceItem(itemId: "%d") { success newEnhanceLevel }
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
                                enhanceItem(itemId: "%d") { success }
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
                                mutation { enhanceItem(itemId: "%d") { success } }
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
                        equipItem(itemId: "%d") { activeSets }
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
                        equipItem(itemId: "%d") { activeSets }
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
                                useItem(itemId: "%d") { healedAmount }
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
                            mutation { useItem(itemId: "%d") { healedAmount } }
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