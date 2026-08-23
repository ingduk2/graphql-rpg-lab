package com.rpg.lab.monster;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
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
    public MonsterResponse monster(
            @InputArgument Long id,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);

        return monsterService.getMonster(id, context.playerId());
    }
}
