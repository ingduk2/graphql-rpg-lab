package com.rpg.lab.battle;

import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BattleService {

    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;

    @Transactional
    public BattleResult attack(Long playerId, Long monsterId) {
        Player player = getPlayer(playerId);
        Monster monster = getMonster(monsterId);

        Battle battle = new Battle(player, monster).attack();

        player.syncHp(battle.getPlayerRemainHp());
        monster.syncHp(battle.getMonsterRemainHp());

        playerRepository.save(player);
        monsterRepository.save(monster);

        return BattleResult.from(battle);
    }

    public BattleResult flee(Long playerId, Long monsterId) {
        Player player = getPlayer(playerId);
        Monster monster = getMonster(monsterId);

        Battle battle = new Battle(player, monster).flee();

        return BattleResult.from(battle);
    }

    private Player getPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));
    }

    private Monster getMonster(Long monsterId) {
        return monsterRepository.findById(monsterId)
                .orElseThrow(() -> new RuntimeException("Monster not found: " + monsterId));
    }
}
