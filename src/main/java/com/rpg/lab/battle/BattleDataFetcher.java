package com.rpg.lab.battle;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class BattleDataFetcher {

    private final BattleService battleService;

    @DgsMutation
    public BattleResult attack(
            @InputArgument Long playerId,
            @InputArgument Long monsterId
    ) {
        return battleService.attack(playerId, monsterId);
    }

    @DgsMutation
    public BattleResult flee(
            @InputArgument Long playerId,
            @InputArgument Long monsterId
    ) {
        return battleService.flee(playerId, monsterId);
    }
}
