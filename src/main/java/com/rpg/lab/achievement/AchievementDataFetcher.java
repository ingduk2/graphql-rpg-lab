package com.rpg.lab.achievement;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class AchievementDataFetcher {

    private final AchievementService achievementService;

    @DgsQuery
    public List<AchievementResponse> myAchievements(DgsDataFetchingEnvironment dfe) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return achievementService.getMyAchievements(context.playerId());
    }
}
