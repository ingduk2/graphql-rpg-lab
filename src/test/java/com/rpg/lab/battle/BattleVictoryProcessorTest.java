package com.rpg.lab.battle;

import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.fixture.QuestFixture;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryItem;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemRepository;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterDrop;
import com.rpg.lab.monster.MonsterDropRepository;
import com.rpg.lab.monster.MonsterRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.quest.*;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class BattleVictoryProcessorTest {

    private final BattleVictoryProcessor sut;

    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;
    private final InventoryRepository inventoryRepository;
    private final MonsterDropRepository monsterDropRepository;
    private final ItemRepository itemRepository;
    private final QuestRepository questRepository;
    private final PlayerQuestRepository playerQuestRepository;

    private Player player;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventoryRepository.save(Inventory.create(player));
    }

    private Battle winningBattle(Monster monster) {
        return new Battle(player, monster, getInventory(), 1, monster.getHp(), monster.getAttackPower()).attack();
    }

    private Inventory getInventory() {
        return inventoryRepository.findByPlayerId(player.getId()).get();
    }

    @Test
    @DisplayName("몬스터 처치 실패 시 보상 없이 HP 만 동기화된다")
    void test1() {
        Monster monster = monsterRepository.save(MonsterFixture.createOrc());
        Battle battle = new Battle(player, monster, getInventory(), Integer.MAX_VALUE, monster.getHp(), monster.getAttackPower()).attack();

        BattleReward result = sut.process(player, battle, monster.getId());

        assertThat(result.levelUps()).isEqualTo(0);
        assertThat(result.droppedItem()).isNull();
        assertThat(result.completedQuests()).isEmpty();
        assertThat(player.getHp()).isEqualTo(battle.getPlayerRemainHp());
    }

    @Test
    @DisplayName("몬스터 처치 성공, 레벨업/드랍/퀘스트 없이 경험치만 오른다")
    void test2() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        int expBefore = player.getExp();
        Battle battle = winningBattle(monster);

        BattleReward result = sut.process(player, battle, monster.getId());

        assertThat(result.levelUps()).isEqualTo(0);
        assertThat(result.droppedItem()).isNull();
        assertThat(result.completedQuests()).isEmpty();
        assertThat(player.getExp()).isEqualTo(expBefore + monster.getExpReward());
    }

    @Test
    @DisplayName("몬스터 처치 성공 + 아이템 드랍 시 보상에 아이템이 담기고 인벤토리에도 반영된다")
    void test3() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item item = itemRepository.save(ItemFixture.createShieldItem());
        monsterDropRepository.save(MonsterDrop.create(monster, item, 1.0));
        Battle battle = winningBattle(monster);

        BattleReward result = sut.process(player, battle, monster.getId());

        assertThat(result.droppedItem()).isEqualTo(item);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems)
                .extracting(InventoryItem::getItem)
                .contains(item);
    }

    @Test
    @DisplayName("몬스터 처치로 퀘스트가 완료되면 보상까지 지급된다")
    void test4() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Quest quest = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));
        playerQuestRepository.save(PlayerQuest.start(player, quest));

        Item rewardItem = itemRepository.save(ItemFixture.createSwordItem());
        quest.addReward(50, rewardItem);

        int expBefore = player.getExp();
        Battle battle = winningBattle(monster);

        BattleReward result = sut.process(player, battle, monster.getId());

        assertThat(result.completedQuests()).contains(quest.getTitle());

        int expectedExp = expBefore + monster.getExpReward() + 50;
        assertThat(player.getExp()).isEqualTo(expectedExp);

        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems)
                .extracting(InventoryItem::getItem)
                .contains(rewardItem);
    }

    @Test
    @DisplayName("몬스터 처치로 레벨업하면 보상에 반영되고 LEVEL_UP 퀘스트 진행도도 오른다")
    void test5() {
        Monster monster = monsterRepository.save(MonsterFixture.createBoss());
        Quest levelUpQuest = questRepository.save(QuestFixture.createLevelUp(1));
        playerQuestRepository.save(PlayerQuest.start(player, levelUpQuest));

        Battle battle = winningBattle(monster);

        BattleReward result = sut.process(player, battle, monster.getId());

        assertThat(result.levelUps()).isGreaterThan(0);
        assertThat(result.completedQuests()).contains(levelUpQuest.getTitle());
    }
}