package com.rpg.lab.battle;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class BattleDataFetcher {

    private final BattleService battleService;

    @DgsMutation
    public BattleResult attack(
            @InputArgument Long monsterId,
            @InputArgument Integer currentMonsterHp,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        Long playerId = context.playerId();
        return battleService.attack(playerId, monsterId, currentMonsterHp);
    }

    @DgsMutation
    public BattleResult flee(
            @InputArgument Long monsterId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        Long playerId = context.playerId();
        return battleService.flee(playerId, monsterId);
    }
}
