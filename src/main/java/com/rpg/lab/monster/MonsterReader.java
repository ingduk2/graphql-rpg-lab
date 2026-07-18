package com.rpg.lab.monster;

import com.rpg.lab.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonsterReader {

    private final MonsterRepository monsterRepository;

    public Monster getById(Long monsterId) {
        return monsterRepository.findById(monsterId)
                .orElseThrow(() -> new EntityNotFoundException("Monster not found: " + monsterId));
    }
}
