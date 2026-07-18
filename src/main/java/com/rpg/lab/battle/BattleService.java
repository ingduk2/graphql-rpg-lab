package com.rpg.lab.battle;

import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryReader;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterReader;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerManager;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BattleService {

    private final PlayerReader playerReader;
    private final PlayerManager playerManager;
    private final MonsterReader monsterReader;
    private final InventoryReader inventoryReader;
    private final BattleVictoryProcessor battleVictoryProcessor;

    @Transactional
    public BattleResult attack(Long playerId, Long monsterId, int currentMonsterHp) {
        Player player = playerReader.getById(playerId);
        Monster monster = monsterReader.getById(monsterId);
        Inventory inventory = inventoryReader.getWithItemsByPlayerId(playerId);

        Battle battle = new Battle(player, monster, inventory, currentMonsterHp).attack();

        BattleReward battleReward = battleVictoryProcessor.process(player, battle, monsterId, playerId);

        return BattleResult.from(battle, battleReward);
    }

    public BattleResult flee(Long playerId, Long monsterId) {
        Player player = playerReader.getById(playerId);
        Monster monster = monsterReader.getById(monsterId);
        Inventory inventory = inventoryReader.getWithItemsByPlayerId(playerId);

        Battle battle = new Battle(player, monster, inventory, monster.getHp()).flee();

        return BattleResult.from(battle, BattleReward.empty());
    }

    @Transactional
    public Flux<BattleEvent> streamBattle(Long playerId, Long monsterId) {
        Player player = playerReader.getById(playerId);
        Monster monster = monsterReader.getById(monsterId);
        Inventory inventory = inventoryReader.getWithItemsByPlayerId(playerId);
        String battleId = playerId + ":" + monsterId;

        return Flux.create(sink -> {
            try {
                int[] monsterHp = {monster.getHp()};
                int[] turn = {1};

                while (monsterHp[0] > 0 && player.getHp() > 0) {
                    Battle battle = new Battle(player, monster, inventory, monsterHp[0]).attack();
                    monsterHp[0] = battle.getMonsterRemainHp();

                    BattleReward battleReward = battleVictoryProcessor.process(player, battle, monsterId, playerId);

                    boolean finished = battle.isMonsterDefeated() || battle.isPlayerDefeated();

                    sink.next(BattleEvent.of(battleId, turn[0]++, battle, battleReward, finished));

                    if (finished) break;

                    Thread.sleep(1000);
                }
                playerManager.save(player);
                sink.complete();
            } catch (Exception e) {
                log.error("Battle stream error: {}", e.getMessage(), e);
                sink.error(e);
            }
        });
    }
}
