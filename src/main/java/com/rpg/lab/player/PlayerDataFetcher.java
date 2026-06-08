package com.rpg.lab.player;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class PlayerDataFetcher {

    private final PlayerService playerService;

    @DgsQuery
    public List<PlayerResponse> players() {
        return playerService.getPlayers();
    }

    @DgsQuery
    public PlayerResponse player(@InputArgument Long id) {
        return playerService.getPlayer(id);
    }
}
