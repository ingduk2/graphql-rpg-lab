package com.rpg.lab.monster;

import com.rpg.lab.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonsterService {

    private final MonsterRepository monsterRepository;

    public List<Monster> getMonsters() {
        return monsterRepository.findAll();
    }

    public Monster getMonster(Long id) {
        return monsterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Monster not found: " + id));
    }
}
