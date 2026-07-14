package com.rpg.lab.battle;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.item.Item;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.quest.QuestProgressUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static com.rpg.lab.quest.QuestType.KILL_MONSTER;
import static com.rpg.lab.quest.QuestType.LEVEL_UP;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BattleService {

    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemDropProcessor itemDropProcessor;
    private final QuestProgressUpdater questProgressUpdater;

    @Transactional
    public BattleResult attack(Long playerId, Long monsterId, int currentMonsterHp) {
        Player player = getPlayer(playerId);
        Monster monster = getMonster(monsterId);
        Inventory inventory = getInventoryWithItems(playerId);

        Battle battle = new Battle(player, monster, inventory, currentMonsterHp).attack();

        player.syncHp(battle.getPlayerRemainHp());

        int levelUps = 0;
        Item droppedItem = null;

        List<String> completedQuests = new ArrayList<>();

        if (battle.isMonsterDefeated()) {
            levelUps = player.gainExp(battle.getExpGained());
            droppedItem = itemDropProcessor.process(monsterId, playerId).orElse(null);
            completedQuests.addAll(questProgressUpdater.update(playerId, KILL_MONSTER, monsterId));

            if (levelUps > 0) {
                completedQuests.addAll(questProgressUpdater.update(playerId, LEVEL_UP, monsterId));
            }
        }

        playerRepository.save(player);

        return BattleResult.from(battle, levelUps, droppedItem, completedQuests);
    }

    public BattleResult flee(Long playerId, Long monsterId) {
        Player player = getPlayer(playerId);
        Monster monster = getMonster(monsterId);
        Inventory inventory = getInventoryWithItems(playerId);

        Battle battle = new Battle(player, monster, inventory, monster.getHp()).flee();

        return BattleResult.from(battle, 0, null, List.of());
    }

    @Transactional
    public Flux<BattleEvent> streamBattle(Long playerId, Long monsterId) {
        Player player = getPlayer(playerId);
        Monster monster = getMonster(monsterId);
        Inventory inventory = getInventoryWithItems(playerId);
        String battleId = playerId + ":" + monsterId;

        return Flux.create(sink -> {
            try {
                int[] monsterHp = {monster.getHp()};
                int[] turn = {1};

                while (monsterHp[0] > 0 && player.getHp() > 0) {
                    Battle battle = new Battle(player, monster, inventory, monsterHp[0]).attack();
                    monsterHp[0] = battle.getMonsterRemainHp();
                    player.syncHp(battle.getPlayerRemainHp());

                    boolean finished = battle.isMonsterDefeated() || battle.isPlayerDefeated();

                    if (battle.isMonsterDefeated()) {
                        player.gainExp(battle.getExpGained());
                        itemDropProcessor.process(monsterId, playerId);
                    }

                    sink.next(new BattleEvent(
                            battleId, turn[0]++,
                            battle.getPlayerDamage(), battle.getMonsterDamage(),
                            battle.getMonsterRemainHp(), battle.getPlayerRemainHp(),
                            battle.isCritical(), battle.isMonsterDefeated(),
                            battle.isPlayerDefeated(), battle.getMessage(),
                            finished
                    ));

                    if (finished) break;

                    Thread.sleep(1000);
                }
                playerRepository.save(player);
                sink.complete();
            } catch (Exception e) {
                log.error("Battle stream error: {}", e.getMessage(), e);
                sink.error(e);
            }
        });
    }

    private Player getPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found: " + playerId));
    }

    private Monster getMonster(Long monsterId) {
        return monsterRepository.findById(monsterId)
                .orElseThrow(() -> new EntityNotFoundException("Monster not found: " + monsterId));
    }

    private Inventory getInventoryWithItems(Long playerId) {
        return inventoryRepository.findWithItemsByPlayerId(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found: " + playerId));
    }
}
