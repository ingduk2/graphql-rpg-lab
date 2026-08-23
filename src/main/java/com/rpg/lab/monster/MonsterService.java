package com.rpg.lab.monster;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonsterService {

    private final MonsterRepository monsterRepository;
    private final PlayerReader playerReader;
    private final MonsterScaler monsterScaler;

    public List<Monster> getMonsters() {
        return monsterRepository.findAll();
    }

    public MonsterResponse getMonster(Long id, Long playerId) {
        Monster monster = monsterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Monster not found: " + id));
        Player player = playerReader.getById(playerId);

        ScaledMonster scaledMonster = monsterScaler.scale(monster, player.getLevel());

        return MonsterResponse.from(monster, scaledMonster);
    }
}
