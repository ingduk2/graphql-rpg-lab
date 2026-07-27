package com.rpg.lab.quest;

import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.fixture.QuestFixture;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryItem;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class QuestRewardProcessorTest {

    private final QuestRewardProcessor sut;
    private final QuestRewardRepository questRewardRepository;
    private final InventoryRepository inventoryRepository;

    private final PlayerRepository playerRepository;
    private final QuestRepository questRepository;
    private final ItemRepository itemRepository;

    private Player player;
    private Quest quest;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        quest = questRepository.save(QuestFixture.createLevelUp(1));
    }

    @Test
    @DisplayName("보상이 exp 만 있는 퀘스트 완료 시 -> Player 의 exp 가 오르는 지 확인")
    void test1() {
        inventoryRepository.save(Inventory.create(player));
        QuestReward questReward = questRewardRepository.save(QuestReward.create(quest, 50, null));
        int expBefore = player.getExp();

        sut.process(player, quest.getId());

        assertThat(player.getExp()).isEqualTo(expBefore + questReward.getRewardExp());
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems).isEmpty();
    }

    @Test
    @DisplayName("보상에 아이템이 포함된 퀘스트 완료 시 -> 인벤토리에 해당 아이템이 추가 되는지 확인")
    void test2() {
        inventoryRepository.save(Inventory.create(player));
        Item item = itemRepository.save(ItemFixture.createSwordItem());
        questRewardRepository.save(QuestReward.create(quest, 0, item));

        sut.process(player, quest.getId());

        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems)
                .extracting(InventoryItem::getItem)
                .contains(item);
    }

    @Test
    @DisplayName("보상 항목이 여러 개 (exp + item 여러 줄)인 퀘스트 완료 시 -> 모든 보상이 다 적용되는지 확")
    void test3() {
        inventoryRepository.save(Inventory.create(player));
        Item item1 = itemRepository.save(ItemFixture.createSwordItem());
        Item item2 = itemRepository.save(ItemFixture.createShieldItem());
        questRewardRepository.save(QuestReward.create(quest, 30, item1));
        questRewardRepository.save(QuestReward.create(quest, 20, item2));
        int expBefore = player.getExp();

        sut.process(player, quest.getId());

        assertThat(player.getExp()).isEqualTo(expBefore + 50);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems)
                .extracting(InventoryItem::getItem)
                .contains(item1, item2);
    }

    @Test
    @DisplayName("이미 보유 중인 아이템이 보상으로 또 나올 때 → 인벤토리에 중복 추가 안 되는지 (개수/목록 그대로인지)")
    void test4() {
        Inventory playerInventory = Inventory.create(player);
        Item item = itemRepository.save(ItemFixture.createSwordItem());
        playerInventory.addItem(item);
        inventoryRepository.save(playerInventory);
        questRewardRepository.save(QuestReward.create(quest, 0, item));
        int expBefore = player.getExp();

        sut.process(player, quest.getId());

        assertThat(player.getExp()).isEqualTo(expBefore);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems).hasSize(1)
                .extracting(InventoryItem::getItem)
                .contains(item);
    }

    @Test
    @DisplayName("해당 questId에 보상이 아예 없는 경우 → 아무 예외 없이 그냥 넘어가는지 (exp 변화 없음)")
    void test5() {
        inventoryRepository.save(Inventory.create(player));
        int expBefore = player.getExp();

        sut.process(player, quest.getId());

        assertThat(player.getExp()).isEqualTo(expBefore);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems).isEmpty();
    }

    @Test
    @DisplayName("player가 인벤토리를 안 가진 상태여도 exp는 정상 지급되고 예외 없이 넘어간다")
    void test6() {
        Item item = itemRepository.save(ItemFixture.createSwordItem());
        QuestReward questReward = questRewardRepository.save(QuestReward.create(quest, 50, item));
        int expBefore = player.getExp();

        sut.process(player, quest.getId());

        assertThat(player.getExp()).isEqualTo(expBefore + questReward.getRewardExp());
        assertThat(inventoryRepository.findByPlayerId(player.getId())).isEmpty();
    }

    private Inventory getInventory() {
        return inventoryRepository.findByPlayerId(player.getId()).get();
    }
}