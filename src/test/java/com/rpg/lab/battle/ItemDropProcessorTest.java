package com.rpg.lab.battle;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
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
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class ItemDropProcessorTest {

    private final ItemDropProcessor sut;
    private final MonsterDropRepository monsterDropRepository;
    private final InventoryRepository inventoryRepository;

    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;
    private final ItemRepository itemRepository;

    private Player player;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventory = inventoryRepository.save(Inventory.create(player));
    }

    @Test
    @DisplayName("dropRate=1.0인 아이템은 반드시 드랍된다")
    void test1() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item item = itemRepository.save(ItemFixture.createShieldItem());
        monsterDropRepository.save(MonsterDrop.create(monster, item, 1.0));

        Optional<Item> result = sut.process(monster.getId(), player.getId());

        assertThat(result).contains(item);
    }

    @Test
    @DisplayName("dropRate=0.0인 아이템은 절대 드랍되지 않는다")
    void test2() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item item = itemRepository.save(ItemFixture.createShieldItem());
        monsterDropRepository.save(MonsterDrop.create(monster, item, 0.0));

        Optional<Item> result = sut.process(monster.getId(), player.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("드랍 성공 시 player 인벤토리에 아이템이 추가된다")
    void test3() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item item = itemRepository.save(ItemFixture.createShieldItem());
        monsterDropRepository.save(MonsterDrop.create(monster, item, 1.0));

        Optional<Item> result = sut.process(monster.getId(), player.getId());

        assertThat(result).contains(item);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems)
                .extracting(InventoryItem::getItem)
                .contains(item);
    }

    @Test
    @DisplayName("이미 보유 중인 아이템이 드랍될 때 인벤토리에 중복 추가되지 않는다")
    void test4() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item item = itemRepository.save(ItemFixture.createShieldItem());
        inventory.addItem(item);
        inventoryRepository.save(inventory);
        monsterDropRepository.save(MonsterDrop.create(monster, item, 1.0));

        Optional<Item> result = sut.process(monster.getId(), player.getId());

        assertThat(result).contains(item);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems).hasSize(1);
    }

    @Test
    @DisplayName("여러 드랍 후보 중 dropRate=1.0인 아이템만 드랍된다")
    void test5() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item nonDropItem = itemRepository.save(ItemFixture.createShieldItem());
        Item dropItem = itemRepository.save(ItemFixture.createSwordItem());
        monsterDropRepository.save(MonsterDrop.create(monster, nonDropItem, 0.0));
        monsterDropRepository.save(MonsterDrop.create(monster, dropItem, 1.0));

        Optional<Item> result = sut.process(monster.getId(), player.getId());

        assertThat(result).contains(dropItem);
        List<InventoryItem> inventoryItems = getInventory().getInventoryItems();
        assertThat(inventoryItems)
                .extracting(InventoryItem::getItem)
                .contains(dropItem);
    }

    @Test
    @DisplayName("드랍 테이블이 없는 monsterId는 예외 없이 Optional.empty()를 반환한다")
    void test6() {
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());

        Optional<Item> result = sut.process(monster.getId(), player.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("인벤토리가 없는 player에게 드랍 발생 시 EntityNotFoundException이 발생한다")
    void test7() {
        Player noInventoryPlayer = playerRepository.save(PlayerFixture.create());
        Monster monster = monsterRepository.save(MonsterFixture.createSlime());
        Item item = itemRepository.save(ItemFixture.createShieldItem());
        monsterDropRepository.save(MonsterDrop.create(monster, item, 1.0));

        assertThatThrownBy(() -> sut.process(monster.getId(), noInventoryPlayer.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Inventory getInventory() {
        return inventoryRepository.findByPlayerId(player.getId()).get();
    }
}