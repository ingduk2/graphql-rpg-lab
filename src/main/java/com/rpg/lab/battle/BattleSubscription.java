package com.rpg.lab.battle;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@DgsComponent
@RequiredArgsConstructor
public class BattleSubscription {

    private final BattleService battleService;

    @DgsSubscription
    public Flux<BattleEvent> battleEvents(
            @InputArgument Long playerId,
            @InputArgument Long monsterId
    ) {
        return battleService.streamBattle(playerId, monsterId);
    }
}
