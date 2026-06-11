package com.rpg.lab.quest;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
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
}
