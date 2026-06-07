package com.rpg.lab.monster;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class MonsterDataFetcher {

    private final MonsterService monsterService;

    @DgsQuery
    public List<MonsterResponse> monsters() {
        List<Monster> monsters = monsterService.getMonsters();

        return monsters.stream()
                .map(MonsterResponse::from)
                .toList();
    }

    @DgsQuery
    public MonsterResponse monster(@InputArgument Long id) {
        Monster monster = monsterService.getMonster(id);

        return MonsterResponse.from(monster);
    }
}
