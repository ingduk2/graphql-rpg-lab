package com.rpg.lab.battle;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@DgsComponent
@RequiredArgsConstructor
public class BattleSubscription {

    private final BattleService battleService;

    @DgsSubscription
    public Flux<BattleEvent> battleEvents(
            @InputArgument Long monsterId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return battleService.streamBattle(context.playerId(), monsterId);
    }
}
