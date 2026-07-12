package com.rpg.lab.quest;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class QuestDataFetcher {

    private final QuestService questService;

    @DgsQuery
    public List<QuestResponse> quests() {
        return questService.getQuests().stream()
                .map(QuestResponse::from)
                .toList();
    }

    @DgsQuery
    public List<PlayerQuestResponse> myQuests(DgsDataFetchingEnvironment dfe) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return questService.getMyQuests(context.playerId());
    }

    @DgsMutation
    public PlayerQuestResponse acceptQuest(
            @InputArgument Long questId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return questService.acceptQuest(context.playerId(), questId);
    }

    @DgsMutation
    public PlayerQuestResponse completeQuest(
            @InputArgument Long questId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return questService.completeQuest(context.playerId(), questId);
    }
}
