package com.rpg.lab.quest;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
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

    @DgsMutation
    public PlayerQuestResponse acceptQuest(
            @InputArgument Long playerId,
            @InputArgument Long questId
    ) {
        return questService.acceptQuest(playerId, questId);
    }

    @DgsMutation
    public PlayerQuestResponse completeQuest(
            @InputArgument Long playerId,
            @InputArgument Long questId
    ) {
        return questService.completeQuest(playerId, questId);
    }
}
